package com.howe.learn.spider.basic.core.platform;

import com.howe.learn.spider.basic.core.extractor.ExtractorTypeEnum;

/**
 * Created by hao on 2017/1/7.
 */
public enum PlatformEnum {

    BAIDU,

    MAFENGWO,

    QYER,

    DEFAULT
    ;

    public ExtractorTypeEnum getScenicExtractor(){
        switch (this) {
            case BAIDU:
                return ExtractorTypeEnum.BAIDU_SCENIC;
            case MAFENGWO:
                return ExtractorTypeEnum.MAFENGWO_SCENIC;
            case QYER:
                return ExtractorTypeEnum.QYER_SCENIC;
        }
        return null;
    }
}
