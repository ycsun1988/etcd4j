package com.zhi.etcd4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.zhi.etcd4j.exception.CompareFailedException;
import com.zhi.etcd4j.exception.DirectoryNotEmptyException;
import com.zhi.etcd4j.exception.EtcdException;
import com.zhi.etcd4j.exception.IndexNotNumberException;
import com.zhi.etcd4j.exception.KeyAlreadyExistsException;
import com.zhi.etcd4j.exception.KeyNotFoundException;
import com.zhi.etcd4j.exception.NotAFileException;
import com.zhi.etcd4j.util.StringUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/6/9.
 */
public class EtcdClientTest {

    private static final Logger LOG = LoggerFactory.getLogger(EtcdClientTest.class);
    private EtcdClient etcdClient;
    private String testDir;

    @Before
    public void setUp() {
        etcdClient = new EtcdClient("http://192.168.64.132");
        testDir = "/" + UUID.randomUUID().toString();
        etcdClient.createDir(testDir);
    }

    @After
    public void tearDown() {
        etcdClient.deleteDir(testDir, true);
    }

    @Test
    public void testCreateEtcdClient() {
        //test EtcdClient(uri)
        String ip = "192.168.64.132";
        new EtcdClient(ip + ":3379");
        new EtcdClient(ip);
        new EtcdClient("http://" + ip);
        new EtcdClient("http://" + ip + ":3379");
        try {
            new EtcdClient("");
            Assert.fail();
        } catch (EtcdException e) {
        }
        //test EtcdClient(uri, port)
    }

    @Test
    public void testSet() {
        //set illegal
        try {
            etcdClient.set("", "hello");
            Assert.fail();
        } catch (EtcdException e) {
        }
        try {
            etcdClient.set("/hello", "");
            Assert.fail();
        } catch (EtcdException e) {
        }
        //set success.
        etcdClient.set(testDir + "/message", "hello world");
    }

    @Test
    public void testSetKeyAndGet() throws Exception {
        String key = testDir + "/message";
        etcdClient.set(key, "hello");
        EtcdResult result = etcdClient.get(key);
        //just get .
        result.getIndex();
        EtcdNode node = result.getNode();
        node.getKey();
        node.getValue();
        node.getCreateIndex();
        node.getExpiration();
        node.getNodes();
        node.getTtl();
        result.getAction();
        result.getPrevNode();
        Assert.assertEquals("hello", result.getNode().getValue());
    }

    @Test
    public void testSetKeyAndGetDir() {
        String key = testDir + "/message";
        etcdClient.set(key, "hello");
        EtcdResult result = etcdClient.get(testDir);
        Assert.assertTrue(result.getNode().isDir());
    }

    @Test
    public void testGetNotFoundKey() {
        String key = testDir + "/notFoundKey";
        try {
            etcdClient.get(key);
            Assert.fail();
        } catch (KeyNotFoundException e) {
            String errorMessage = e.getMessage();
            LOG.debug("{}", errorMessage);
            Assert.assertTrue(StringUtil.isNotEmpty(errorMessage));
        }
    }

    @Test
    public void testUpdateKey() {
        String key = testDir + "/message";
        String value1 = "hello";
        String value2 = "hello2222";
        etcdClient.set(key, value1);
        etcdClient.set(key, value2);
        EtcdResult result = etcdClient.get(key);
        Assert.assertTrue(result.getNode().getValue().equals(value2));
    }

    @Test
    public void testDeleteKey() {
        String key = testDir + "/message";
        //delete a not found key
        try {
            etcdClient.delete(key);
            Assert.fail();
        } catch (KeyNotFoundException e) {
        }
        //delete an exist key
        etcdClient.set(key, "hello, world");
        etcdClient.delete(key);
        try {
            etcdClient.get(key);
            Assert.fail();
        } catch (KeyNotFoundException e) {
        }
    }

    @Test
    public void testSetDirExpire() {
        String key = testDir + "/message";
        //test set exists key's expire time.
        etcdClient.set(key, "hello, world", 2);
        sleep(TimeUnit.SECONDS, 3);
        try {
            etcdClient.get(key);
            Assert.fail();
        } catch (KeyNotFoundException e) {
        }
        //test update a not found key.
        key = testDir + "/notFoundKey";
        try {
            etcdClient.set(key, "hello, world", 3, true);
            Assert.fail();
        } catch (KeyNotFoundException e) {
        }
    }

    @Test
    public void testTtl() {
        String key = testDir + "/message";
        try {
            etcdClient.ttl(key, -1);
            Assert.fail();
        } catch (KeyNotFoundException e) {
        }
        etcdClient.set(key, "hello, world");
        etcdClient.ttl(key, 2);
        sleep(TimeUnit.SECONDS, 3);
        try {
            etcdClient.get(key);
        } catch (KeyNotFoundException e) {
        }
        //test unset a key's ttl
        String key2 = "/message2";
        etcdClient.set(key2, "hello, world2", 2);
        etcdClient.ttl(key2, -1);
        sleep(TimeUnit.SECONDS, 3);
        etcdClient.get(key2);
    }

    //test ttl a dir
    @Test
    public void testTtl2() {
        String subDir = testDir + "/subDir01";
        //test ttl a not exist dir
        try {
            etcdClient.ttl(subDir, 3);
            Assert.fail();
        } catch (KeyNotFoundException e) {
        }
        //test ttl a exist dir, but not tell etcd that is a dir.
        etcdClient.createDir(subDir);
        etcdClient.set(subDir + "/key011", "hello, world");
        //test ttl a exist dir, and test etcd that is a dir
        etcdClient.ttl(subDir, 2);
        sleep(TimeUnit.SECONDS, 3);
        try {
            etcdClient.get(subDir);
            Assert.fail();
        } catch (KeyNotFoundException e) {
        }
    }

    @Test
    public void testCreateDir() {
        String subDir = testDir + "/dir01";
        etcdClient.createDir(subDir);
        EtcdResult result = etcdClient.get(subDir);
        Assert.assertTrue(result.getNode().isDir());
        //test create dir with ttl
        String subDir2 = testDir + "/dir02";
        etcdClient.createDir(subDir2, 2);
        sleep(TimeUnit.SECONDS, 3);
        try {
            etcdClient.get(subDir2);
            Assert.fail();
        } catch (KeyNotFoundException e) {
        }
        //test update dir error
        try {
            etcdClient.createDir(subDir, 1, false);
            Assert.fail();
        } catch (NotAFileException e) {
        }
        //test update dir success
        etcdClient.createDir(subDir, 2, true);
        sleep(TimeUnit.SECONDS, 3);
        try {
            etcdClient.get(subDir);
            Assert.fail();
        } catch (KeyNotFoundException e) {
        }
        //test create dirs,such as /testDir/subDir01/subDir011
        String subDir011 = testDir + "/subDir02/subDir022";
        etcdClient.createDir(subDir011);
        result = etcdClient.get(testDir + "/subDir02");
        Assert.assertTrue(result.getNode().isDir());
        result = etcdClient.get(subDir011);
        Assert.assertTrue(result.getNode().isDir());
    }

    @Test
    public void testListDir() {
        String subDir = testDir + "/subDir01";
        etcdClient.createDir(subDir);
        //test list a empty dir.
        EtcdResult result = etcdClient.listDir(subDir);
        Assert.assertTrue(result.getNode().getNodes() == null);

        //test list a dir with two keys.
        etcdClient.set(subDir + "/key01", "hello,world");
        etcdClient.set(subDir + "/key02", "hello,world2222");
        result = etcdClient.listDir(subDir);
        Assert.assertTrue(result.getNode().getNodes().size() == 2);

        //test recursive list a dir with two keys.
        etcdClient.set(subDir + "/dir03/key03", "blablabla");
        result = etcdClient.listDir(subDir, true);
        EtcdNode dir03Node = null;
        for (EtcdNode node : result.getNode().getNodes()) {
            if (node.getKey().endsWith("/dir03")) {
                dir03Node = node;
                break;
            }
        }
        Assert.assertTrue(dir03Node.getNodes().get(0).getValue().equals("blablabla"));
    }

    @Test
    public void testListNotExistDir() {
        String subDir = testDir + "/subDir";
        try {
            etcdClient.listDir(subDir);
            Assert.fail();
        } catch (KeyNotFoundException e) {
        }
    }

    @Test
    public void testDeleteDir() {
        String subDir = testDir + "/subDir";
        //test not exist dir
        try {
            etcdClient.deleteDir(subDir, false);
            Assert.fail();
        } catch (KeyNotFoundException e) {
        }
        //test delete a empty dir
        etcdClient.createDir(subDir);
        etcdClient.deleteDir(subDir, false);
        try {
            etcdClient.get(subDir);
            Assert.fail();
        } catch (KeyNotFoundException e) {
        }
        //test delete a dir with 3 keys, but not tell etcd delete dir recursively.
        etcdClient.createDir(subDir);
        etcdClient.set(subDir + "/key01", "hello, world");
        etcdClient.set(subDir + "/key02", "hello, world2");
        try {
            etcdClient.deleteDir(subDir, false);
            Assert.fail();
        } catch (DirectoryNotEmptyException e) {
        }

        //test delete dir with 3 key. and tell etcd delete dir recursively.
        etcdClient.deleteDir(subDir, true);
        try {
            etcdClient.get(subDir);
            Assert.fail();
        } catch (KeyNotFoundException e) {
        }
    }

    //test prevExist
    @Test
    public void testCas() {
        String key = testDir + "/key01";
        String value = "hello, world";
        String afterValue = "after value";
        etcdClient.set(key, value);
        Map<String, String> conditions = new HashMap<String, String>();
        conditions.put(EtcdClient.KEY_OF_PREVEXIST, String.valueOf(false));
        try {
            etcdClient.cas(key, afterValue, conditions);
            Assert.fail();
        } catch (KeyAlreadyExistsException e) {
        }
        conditions.clear();
        conditions.put(EtcdClient.KEY_OF_PREVEXIST, String.valueOf(true));
        etcdClient.cas(key, afterValue, conditions);
        EtcdResult result = etcdClient.get(key);
        Assert.assertTrue(result.getNode().getValue().equals(afterValue));
    }

    //test prevIndex
    @Test
    public void testCas2() {
        String key = testDir + "/key01";
        String value = "hello, world";
        //set key
        EtcdResult result = etcdClient.set(key, value);
        int index = result.getIndex();
        String afterValue = "after value222";
        Map<String, String> conditions = new HashMap<String, String>();
        //test cas error
        conditions.put(EtcdClient.KEY_OF_PREVINDEX, String.valueOf(index + 1));
        try {
            etcdClient.cas(key, afterValue, conditions);
            Assert.fail();
        } catch (CompareFailedException e) {
        }

        //test cas success.
        conditions.clear();
        conditions.put(EtcdClient.KEY_OF_PREVINDEX, String.valueOf(index));
        etcdClient.cas(key, afterValue, conditions);
        result = etcdClient.get(key);
        Assert.assertTrue(result.getNode().getValue().equals(afterValue));

    }

    //test prevValue
    @Test
    public void testCas3() {
        String key = testDir + "/key01";
        String value = "hello, world";
        String afterValue = "after value";
        etcdClient.set(key, value);
        Map<String, String> conditions = new HashMap<String, String>();
        conditions.put(EtcdClient.KEY_OF_PREVVALUE, "not this value");
        try {
            etcdClient.cas(key, afterValue, conditions);
            Assert.fail();
        } catch (CompareFailedException e) {
        }

        //test success.
        conditions.clear();
        conditions.put(EtcdClient.KEY_OF_PREVVALUE, value);
        //实际上从cas的返回值就可以知道cas操作是否执行成功。
        etcdClient.cas(key, afterValue, conditions);
        EtcdResult result = etcdClient.get(key);
        Assert.assertTrue(result.getNode().getValue().equals(afterValue));

    }

    // test cas a dir error
    @Test
    public void testCas4() {
        String key = testDir + "/subDir";
        etcdClient.createDir(key);
        //test cas a empty dir
        Map<String, String> conditions = new HashMap<String, String>();
        conditions.put(EtcdClient.KEY_OF_PREVEXIST, String.valueOf(true));
        try {
            etcdClient.cas(key, "blabla", conditions);
            Assert.fail();
        } catch (NotAFileException e) {
        }
        //test cas a dir with 1 key
        etcdClient.set(key + "/key01", "hello, world");
        try {
            etcdClient.cas(key, "afafdafafafa", conditions);
            Assert.fail();
        } catch (NotAFileException e) {
        }
    }

    //test cad with pervExist condition, note: Etcd's cad api not support prevExist condition!
    @Test
    public void testCad() {
        //test cad with no conditions, Etcd will delete exist key.
        String key = testDir + "/key01";
        String value = "hello, world";
        etcdClient.set(key, value);
        Map<String, String> conditions = new HashMap<String, String>();
        conditions.put(EtcdClient.KEY_OF_PREVEXIST, String.valueOf(false));
        etcdClient.cad(key, conditions);
        try {
            etcdClient.get(key);
            Assert.fail();
        } catch (KeyNotFoundException e) {
        }

    }

    //test cad with pervValue condition, note: Etcd's cad api not support prevExist condition!
    @Test
    public void testCad2() {
        //test cad with pervValue condition, which prevValue is not equal with actually value.
        String key = testDir + "/key01";
        String value = "hello, world";
        etcdClient.set(key, value);
        Map<String, String> conditions = new HashMap<String, String>();
        conditions.put(EtcdClient.KEY_OF_PREVVALUE, "afafafdaf");
        try {
            etcdClient.cad(key, conditions);
            Assert.fail();
        } catch (CompareFailedException e) {
        }

        //test cad with prevValue condition, which prevValue is equal with actually value.
        conditions.clear();
        conditions.put(EtcdClient.KEY_OF_PREVVALUE, value);
        etcdClient.cad(key, conditions);
        try {
            etcdClient.get(key);
            Assert.fail();
        } catch (KeyNotFoundException e) {
        }
    }

    //test cad with pervIndex condition, note: Etcd's cad api not support prevExist condition!
    @Test
    public void testCad3() {
        //test cad with pervIndex condition, which prevIndex is not equal with actual value.
        String key = testDir + "/key01";
        String value = "hello, world";
        EtcdResult result = etcdClient.set(key, value);
        int index = result.getIndex();
        Map<String, String> conditions = new HashMap<String, String>();
        //test index not a number.
        conditions.put(EtcdClient.KEY_OF_PREVINDEX, "abcdddd");
        try {
            etcdClient.cad(key, conditions);
            Assert.fail();
        } catch (IndexNotNumberException e) {
        }
        //test index not the actual value.
        conditions.clear();
        conditions.put(EtcdClient.KEY_OF_PREVINDEX, String.valueOf(index + 1));
        try {
            etcdClient.cad(key, conditions);
            Assert.fail();
        } catch (CompareFailedException e) {
        }

        //test cad with prevIndex condition, which prevIndex is equal with actual value.
        conditions.clear();
        conditions.put(EtcdClient.KEY_OF_PREVINDEX, String.valueOf(index));
        etcdClient.cad(key, conditions);
        try {
            etcdClient.get(key);
            Assert.fail();
        } catch (KeyNotFoundException e) {
        }
    }

    //test watch on a key.
    //note: watch api return only one change after the time point watched on a key.
    @Test
    public void testWatch() {
        final String key = testDir + "/key01";
        String value = "hello, world";
        final String afterValue = "Rocket boy.";
        etcdClient.set(key, value);
        etcdClient.watch(key, new EtcdCallback() {
            public void onFailure(EtcdException e) {
                LOG.error("Watch on key [{}] error.", key, e);
            }

            public void onResponse(EtcdResult result) {
                LOG.info("TestDir: {}", testDir);
                LOG.info("Response str: {}", result);
                //BugFix: 当EtcdCallback回调的时候, testWatch 方法已经销毁，导致afterValue 为空
                //刚好与result对应的是testWatch 方法执行结束后，tearDown() 方法删除/testDir 下面的所有key
                //对应的时间
//                Assert.assertTrue(result.getNode().getValue().equals(afterValue));
                Assert.assertTrue(result.getNode().getValue().equals(afterValue));
            }
        });
        //start modify key thread.
        Thread modifyKeyThread = new Thread(new Runnable() {
            public void run() {
                sleep(TimeUnit.SECONDS, 2);
                etcdClient.set(key, afterValue);
            }
        });
        modifyKeyThread.start();
        //note: must sleep to avoid watch the tearDown event.
        sleep(TimeUnit.SECONDS, 3);
    }

    //test watch on a dir key with recursive parameter is false
    //note: watch api return only one change after the time point watched on a key.
    @Test
    public void testWatch2() {
        final String subDir = testDir + "/subDir0001";
        etcdClient.createDir(subDir);
        final String key01 = subDir + "/key01";
        String value01 = "hello, world";
        final String afterValue01 = "hello, world, modified.";
        String key02 = subDir + "/key02";
        String value02 = "value0000022";
        etcdClient.set(key01, value01);
        etcdClient.set(key02, value02);
        //test watch on a dir, with recursive parameter is false.
        etcdClient.watch(subDir, false, new EtcdCallback() {
            public void onFailure(EtcdException e) {
                LOG.error("Watch on key [{}] error.", subDir, e);
            }

            public void onResponse(EtcdResult result) {
                Assert.assertFalse("hello, world, modified.".equals(result.getNode().getValue()));
            }
        });
        Thread modifiedKey01Thread = new Thread(new Runnable() {
            public void run() {
                sleep(TimeUnit.SECONDS, 2);
                etcdClient.set(key01, afterValue01);
            }
        });
        modifiedKey01Thread.start();
        sleep(TimeUnit.SECONDS, 3);
    }

    //test watch on a dir, with recursive parameter is true.
    //note: watch api return only one change after the time point watched on a key.
    @Test
    public void testWatch3() {
        final String subDir = testDir + "/subDir0001";
        etcdClient.createDir(subDir);
        final String key01 = subDir + "/key01";
        String value01 = "hello, world";
        final String afterValue01 = "hello, world, modified.";
        String key02 = subDir + "/key02";
        String value02 = "value0000022";
        etcdClient.set(key01, value01);
        etcdClient.set(key02, value02);
        //test watch on a dir, with recursive parameter is false.
        etcdClient.watch(subDir, true, new EtcdCallback() {
            public void onFailure(EtcdException e) {
                LOG.error("Watch on key [{}] error.", subDir, e);
            }

            public void onResponse(EtcdResult result) {
                Assert.assertTrue(afterValue01.equals(result.getNode().getValue()));
            }
        });
        Thread modifiedKey01Thread = new Thread(new Runnable() {
            public void run() {
                sleep(TimeUnit.SECONDS, 2);
                etcdClient.set(key01, afterValue01);
            }
        });
        modifiedKey01Thread.start();
        sleep(TimeUnit.SECONDS, 3);
    }

    //test watch a key with specific wait index
    @Test
    public void testWatch04() {
        final String key = testDir + "/key0001";
        String value = "testWatch04";
        final String afterValue = "after testWatch04";
        EtcdResult result = etcdClient.set(key, value);
        //watch first change of key.
        etcdClient.watch(key, result.getIndex(), new EtcdCallback() {
            public void onFailure(EtcdException e) {
                LOG.error("Watch on key [{}] error.", key, e);
            }

            public void onResponse(EtcdResult result) {
                Assert.assertTrue(afterValue.equals(result.getNode().getValue()));
            }
        });
        //test modify key firstly.
        result = etcdClient.set(key, afterValue);

        //test modify key secondly.
        final String value03 = "testWatch04 modified secondly.";
        Thread secondModifyKeyThread = new Thread(new Runnable() {
            public void run() {
                sleep(TimeUnit.SECONDS, 2);
                etcdClient.set(key, value03);
            }
        });
        secondModifyKeyThread.start();
        //watch second change of key
        etcdClient.watch(key, result.getIndex(), new EtcdCallback() {
            public void onFailure(EtcdException e) {
                LOG.error("Watch on key [{}] error.", key, e);
            }

            public void onResponse(EtcdResult result) {
                Assert.assertTrue(value03.equals(result.getNode().getValue()));
            }
        });
        sleep(TimeUnit.SECONDS, 3);
    }
//    //test watch a dir key with specific wait index
//    // no need test.
//    @Test
//    public void testWatch05() {
//
//    }

    //test EtcdNode set get, just for test coverage.
    @Test
    public void testEtcdNode() {
        EtcdNode etcdNode = new EtcdNode();
        etcdNode.setCreateIndex(0L);
        etcdNode.setDir(true);
        etcdNode.setExpiration("2017-06-17");
        etcdNode.setKey("/key01");
        etcdNode.setTtl(3);
        etcdNode.setValue("blablabla");
        etcdNode.setModifiedIndex(1L);
        etcdNode.setNodes(null);
        etcdNode.getModifiedIndex();
    }

    private void sleep(TimeUnit unit, int sleep) {
        try {
            unit.sleep(sleep);
        } catch (InterruptedException e) {
        }
    }
}
