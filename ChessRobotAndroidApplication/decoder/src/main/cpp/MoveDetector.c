#include <jni.h>

#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>

#include <stdio.h>


AVFormatContext *pFormatCtx = NULL;
int i, videoStream;
AVCodecContext *pCodecCtx = NULL;
AVCodec *pCodec = NULL;
AVFrame *pFrame = NULL;

int frameFinished;
AVDictionary *optionsDict = NULL;
struct SwsContext *sws_ctx = NULL;


#define FRAME_FREQUENCY 3
#define THRESHOLD 12000 // change

uint8_t previousBoard[240][240];
uint8_t currentBoard[240][240];
int flag = 0;
int sum = 0;

void initializePreviousBoard(AVFrame *pFrame) {
    for (int i = 120; i < 360; ++i) {
        for (int j = 200; j < 440; ++j) {
            previousBoard[i - 120][j - 200] = *(pFrame->data[0] + i * 640 + j);
        }
    }
}

void initializeCurrentBoard(AVFrame *pFrame) {
    for (int i = 120; i < 360; ++i) {
        for (int j = 200; j < 440; ++j) {
            currentBoard[i - 120][j - 200] = *(pFrame->data[0] + i * 640 + j);
        }
    }
}

int countDifference(AVFrame *pFrame) {
    sum = 0;
    initializeCurrentBoard(pFrame);

    for (int i = 0; i < 240; ++i) {
        for (int j = 0; j < 240; ++j) {
            if ((previousBoard[i][j] - currentBoard[i][j]) *
                (previousBoard[i][j] - currentBoard[i][j]) > 25) {
                sum += 1;
            }
        }
    }

    for (int i = 0; i < 240; ++i) {
        for (int j = 0; j < 240; ++j) {
            previousBoard[i][j] = currentBoard[i][j];
        }
    }

    return sum;
}


JNIEXPORT jboolean JNICALL
Java_ru_nt202_decoder_MoveDetection_moveHappened(JNIEnv *env, jclass type, jstring url_) {
    const char *url = (*env)->GetStringUTFChars(env, url_, 0);

    pFormatCtx = NULL;
    pCodecCtx = NULL;
    i = 0;
    videoStream = 0;

    pCodec = NULL;
    pFrame = NULL;
    AVPacket packet;
    frameFinished = 0;
    optionsDict = NULL;
    sws_ctx = NULL;


    av_register_all();
    avformat_network_init();

    if (avformat_open_input(&pFormatCtx, url, NULL, NULL) != 0) {
        return 0;
    }

    if (avformat_find_stream_info(pFormatCtx, NULL) < 0) {
        return 0;
    }

    av_dump_format(pFormatCtx, 0, url, 0);

    videoStream = -1;
    for (i = 0; i < pFormatCtx->nb_streams; i++) {
        if (pFormatCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
            videoStream = i;
            break;
        }
    }
    if (videoStream == -1) {
        return 0;
    }

    pCodecCtx = pFormatCtx->streams[videoStream]->codec;

    pCodec = avcodec_find_decoder(pCodecCtx->codec_id);
    if (pCodec == NULL) {
        return 0;
    }

    if (avcodec_open2(pCodecCtx, pCodec, &optionsDict) < 0) {
        return 0;
    }

    pFrame = av_frame_alloc();

    sws_ctx = sws_getContext(pCodecCtx->width, pCodecCtx->height, pCodecCtx->pix_fmt,
                             pCodecCtx->width, pCodecCtx->height, PIX_FMT_YUV420P,
                             SWS_BILINEAR, NULL, NULL, NULL);

    int counter = FRAME_FREQUENCY + 1;

    while (av_read_frame(pFormatCtx, &packet) >= 0) {
        if (packet.stream_index == videoStream) {
            avcodec_decode_video2(pCodecCtx, pFrame, &frameFinished, &packet);
            if (frameFinished) {
                if (counter == FRAME_FREQUENCY + 1) {
                    sws_scale(sws_ctx, (uint8_t const *const *) pFrame->data,
                              pFrame->linesize, 0, pCodecCtx->height,
                              pFrame->data, pFrame->linesize);

                    initializePreviousBoard(pFrame);
                    counter = 0;
                } else {
                    if (counter == FRAME_FREQUENCY) {
                        sws_scale(sws_ctx, (uint8_t const *const *) pFrame->data,
                                  pFrame->linesize, 0, pCodecCtx->height,
                                  pFrame->data, pFrame->linesize);

                        int differenceSum = countDifference(pFrame);
                        if (differenceSum > THRESHOLD && flag == 0) {
                            flag = 1;
                        } else {
                            if (differenceSum < THRESHOLD / 40 && flag == 1) {
                                flag = 0;
                                break;
                            }
                        }
                        counter = 0;
                    } else {
                        counter++;
                    }
                }
            }
        }
        av_free_packet(&packet);
    }

    av_free_packet(&packet);
    (*env)->ReleaseStringUTFChars(env, url_, url);
    av_free(pFrame); // Free the YUV frame
    avcodec_close(pCodecCtx);
    avformat_close_input(&pFormatCtx);

    return 1;
}