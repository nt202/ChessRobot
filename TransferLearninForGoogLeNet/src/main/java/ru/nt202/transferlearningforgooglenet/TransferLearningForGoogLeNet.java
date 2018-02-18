package ru.nt202.transferlearningforgooglenet;

import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.image.loader.BaseImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.transferlearning.FineTuneConfiguration;
import org.deeplearning4j.nn.transferlearning.TransferLearning;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.GoogLeNet;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class TransferLearningForGoogLeNet {
    public static void main(String[] args) throws IOException {
        Logger log = LoggerFactory.getLogger(TransferLearningForGoogLeNet.class); // Создание логгера

        int numberOfClasses = 13; // Количество классов на выходе
        int randomSeed = 777; // Случайное значение
        int nEpochs = 5; // Количество эпох обучения
        int imageHeight = 224; // Высота входного изображения
        int imageWidth = 224; // Ширина входного изображения
        int nChannels = 3; // Количество каналов входного изображения
        int miniBatchSize = 10; // Количество примеров для каждой mini-batch
        int labelIndex = 1; // Всегда 1 для ImageRecordReader

        ZooModel zooModel = new GoogLeNet(); // Инициализация модели GoogLeNet
        Model modelGoogLeNet = zooModel.initPretrained(PretrainedType.IMAGENET); // Помещение весов от ImageNet

        // Конфигурация параметров обучения:
        FineTuneConfiguration fineTuneConf = new FineTuneConfiguration.Builder()
                .learningRate(5e-5)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(Updater.NESTEROVS)
                .seed(randomSeed)
                .build();

        // Создание вычислительного графа на основе модели, путем ее изменения:
        ComputationGraph graphGoogLeNet = new TransferLearning.GraphBuilder((ComputationGraph) modelGoogLeNet)
                .fineTuneConfiguration(fineTuneConf)
                .setFeatureExtractor("dropout_13") // the specified layer and below are "frozen"
                .setFeatureExtractor("dropout_14") // the specified layer and below are "frozen"
                .setFeatureExtractor("dropout_15") // the specified layer and below are "frozen"
                .removeVertexKeepConnections("loss1/classifier")
                .removeVertexKeepConnections("loss2/classifier")
                .removeVertexKeepConnections("loss3/classifier")
                .removeVertexAndConnections("prob")
                .addLayer("loss1/classifier", new DenseLayer.Builder().nIn(1024).nOut(numberOfClasses).build(), "dropout_13")
                .addLayer("loss2/classifier", new DenseLayer.Builder().nIn(1024).nOut(numberOfClasses).build(), "dropout_14")
                .addLayer("predictions",
                        new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                                .nIn(1024).nOut(numberOfClasses)
                                .weightInit(WeightInit.XAVIER)
                                .activation(Activation.SOFTMAX).build(), "dropout_15")
                .setOutputs("predictions")
                .build();

        log.info(graphGoogLeNet.summary()); // Логгирование характеристики вычислительного графа

        // Загрузка данных для тренировки и тестирования:
        File trainData = new File("/home/user/Workspace/ChessRobot/TransferLearninForGoogLeNet/src/main/resources/pieces/output_train");
        File testData = new File("/home/user/Workspace/ChessRobot/TransferLearninForGoogLeNet/src/main/resources/pieces/output_test");

        String[] allowedExtensions = BaseImageLoader.ALLOWED_FORMATS;
        Random rng = new Random(randomSeed);

        // Деление изображений:
        FileSplit train = new FileSplit(trainData, allowedExtensions, rng);
        FileSplit test = new FileSplit(testData, allowedExtensions, rng);

        // Логгирование числа тренеровочных и тестовых данных:
        log.info("Total training images is " + train.length());
        log.info("Total test images is " + test.length());

        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator(); // ?Здесь будут храниться выводы о принадлежности к классу

        ImageRecordReader trainRR = new ImageRecordReader(imageHeight, imageWidth, nChannels, labelMaker);
        trainRR.initialize(train);
        DataSetIterator trainIter = new RecordReaderDataSetIterator(trainRR, miniBatchSize, labelIndex, numberOfClasses);
//        MultiDataSetIterator trainIter = new RecordReaderMultiDataSetIterator.Builder(miniBatchSize)
//                .addReader("trainRR", trainRR).addInput("trainRR", 0, 0)
//                .addOutputOneHot("trainRR", 1, numberOfClasses)
//                .addOutputOneHot("trainRR", 1, numberOfClasses)
//                .addOutputOneHot("trainRR", 1, numberOfClasses)
//                .build();

        ImageRecordReader testRR = new ImageRecordReader(imageHeight, imageWidth, nChannels, labelMaker);
        testRR.initialize(test);
        DataSetIterator testIter = new RecordReaderDataSetIterator(testRR, miniBatchSize, labelIndex, numberOfClasses);

        graphGoogLeNet.setListeners(new ScoreIterationListener(1)); //Print score every 1 parameter updates

//         Показ хода обучения в браузере:
//        UIServer uiServer = UIServer.getInstance();
//        StatsStorage statsStorage = new InMemoryStatsStorage();
//        uiServer.attach(statsStorage);
//        graphGoogLeNet.setListeners(new StatsListener(statsStorage));

        // Обучение графа:
        for (int n = 1; n <= nEpochs; n++) {
            log.info("*** Start epoch {} ***", n);
            while (trainIter.hasNext()) {
                graphGoogLeNet.fit(trainIter.next());
            }
            log.info("*** Completed epoch {} ***", n);
            trainIter.reset();
            saveModel(n, log, graphGoogLeNet);
        }


        // Тестирование последней модели:
        log.info("Evaluating model....");
        Evaluation eval = graphGoogLeNet.evaluate(testIter);
        while (testIter.hasNext()) {
            eval = graphGoogLeNet.evaluate(testIter);
        }
        log.info(eval.stats() + "\n");
        testIter.reset();
    }

    private static void saveModel(int i, Logger log, ComputationGraph graphGoogLeNet) {
        // Сохранение модели:
        log.info("Saving model....");
        File locationToSave = new File("/home/user/Workspace/ChessRobot/TransferLearninForGoogLeNet/src/main/resources/" + i + "modelGoogLeNet.zip");
        try {
            ModelSerializer.writeModel(graphGoogLeNet, locationToSave, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}