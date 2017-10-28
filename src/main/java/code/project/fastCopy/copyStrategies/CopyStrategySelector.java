package code.project.fastCopy.copyStrategies;

import code.project.fastCopy.copyStrategies.impl.*;
import code.project.fastCopy.data.CopyStrategy;

public class CopyStrategySelector {
    public Copy getCopyObjectByCopyStrategy(CopyStrategy copyStrategy){
        switch (copyStrategy){
            case FILES_PATH:
                return new PathCopy();
            case NIO_BUFFER:
                return new NioBufferCopy();
            case NIO_TRANSFER:
                return new NioTransferCopy();
            case NATIVE_COPY:
                return new NativeCopy();
            case NATIVE_READER:
                return new NativeReaderCopy();
            case BUFFERED_READER:
                return new BufferedReaderCopy();
            case NATIVE_STREAMS:
                return new NativeStreamsCopy();
            case BUFFERED_STREAMS:
                return new BufferedStreamsCopy();
            case CUSTOM_BUFFER_READER:
                return new CustomBufferBufferedReaderCopy();
            case CUSTOM_BUFFER_STREAMS:
                return new CustomBufferBufferedStreamsCopy();
            case CUSTOM_BUFFER_BUFFERED_READER:
                return new CustomBufferBufferedReaderCopy();
            case CUSTOM_BUFFER_BUFFERED_STREAMS:
                return new CustomBufferBufferedStreamsCopy();
            default:
                return null;
        }
    }
}
