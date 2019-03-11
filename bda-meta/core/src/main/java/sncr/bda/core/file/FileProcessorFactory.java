package sncr.bda.core.file;

public class FileProcessorFactory {

  public static FileProcessor getFileProcessor(String path) {
    FileProcessor processor;

    if (path.startsWith(FileProcessor.maprFsPrefix)) {
      processor = new HFileProcessor();
    } else {
      processor = new NioFileProcessor();
    }

    return processor;

  }
}