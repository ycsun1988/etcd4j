package com.zhi.etcd4j.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/6/14.
 */
public class EtcdExceptionUtil {

    private static Map<Integer, IGenerateException> exceptionGeneratorMap
            = new HashMap<Integer, IGenerateException>();

    static {
        exceptionGeneratorMap.put(EtcdErrorCodes.ERROR_CODE_KEY_NOT_FOUND, new IGenerateException() {
            public EtcdException generate(String message) {
                return new KeyNotFoundException(message);
            }
        });
        exceptionGeneratorMap.put(EtcdErrorCodes.ERROR_CODE_NOT_A_FILE, new IGenerateException() {
            public EtcdException generate(String message) {
                return new NotAFileException(message);
            }
        });
        exceptionGeneratorMap.put(EtcdErrorCodes.ERROR_CODE_DIRECTORY_NOT_EMPTY, new IGenerateException() {
            public EtcdException generate(String message) {
                return new DirectoryNotEmptyException(message);
            }
        });
        exceptionGeneratorMap.put(EtcdErrorCodes.ERROR_CODE_COMPARE_FAILED, new IGenerateException() {
            public EtcdException generate(String message) {
                return new CompareFailedException(message);
            }
        });
        exceptionGeneratorMap.put(EtcdErrorCodes.ERROR_CODE_KEY_ALREADY_EXISTS, new IGenerateException() {
            public EtcdException generate(String message) {
                return new KeyAlreadyExistsException(message);
            }
        });
        exceptionGeneratorMap.put(EtcdErrorCodes.ERROR_CODE_INDEX_NOT_NUMBER, new IGenerateException() {
            public EtcdException generate(String message) {
                return new IndexNotNumberException(message);
            }
        });
        //for other exceptions.
    }

    public static EtcdException newSpecificException(int errorCode, String message) {
        IGenerateException exceptionGenerator = exceptionGeneratorMap.get(errorCode);
        if (exceptionGenerator != null) {
            return exceptionGenerator.generate(message);
        }
        //return common etcd exception.
        return new EtcdException(message);
    }
}
