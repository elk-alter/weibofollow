package com.elk.util;

import com.elk.bean.User;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HBaseUtil {
    private Configuration configuration = null;
    Connection connection = null;

    public void getCon() {
        System.out.println("准备连接");
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        configuration.set("hbase.zookeeper.quorum", "192.168.119.128");
        try {
            connection = ConnectionFactory.createConnection(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(" 开启连接");
    }

    /**
     *
     * @param rowKey
     * @param followKey
     * @return 是否关注
     * @throws IOException
     */
    public boolean isFollow(String rowKey, String followKey) throws IOException {
        TableName tableName = TableName.valueOf("relation");
        int num = 0;

        Table table = connection.getTable(tableName);
        Get get = new Get(Bytes.toBytes(rowKey));
        get.addFamily(Bytes.toBytes("follower"));
        Result result = table.get(get);
        for (Cell cell : result.rawCells()) {
            if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals(followKey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加关注
     * @param rowKey
     * @param followKey
     * @throws IOException
     */
    public void addFollower(String rowKey, String followKey) throws IOException {
        TableName tableName = TableName.valueOf("relation");

        Table table = connection.getTable(tableName);
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes("follower"), Bytes.toBytes(followKey), Bytes.toBytes(""));
        System.out.println(rowKey + "关注了" + followKey);
        table.put(put);

        table.close();
    }

    /**
     * 添加粉丝
     * @param rowKey
     * @param fanKey
     * @throws IOException
     */
    public void addFans(String rowKey, String fanKey) throws IOException {
        TableName tableName = TableName.valueOf("relation");

        Table table = connection.getTable(tableName);
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes("fans"), Bytes.toBytes(fanKey), Bytes.toBytes(""));
        System.out.println(fanKey + "成为了" + rowKey + "的粉丝");
        table.put(put);

        table.close();
    }

    /**
     * 删除关注者
     * @param rowKey
     * @param followKey
     * @throws IOException
     */
    public void delFollower(String rowKey, String followKey) throws IOException {
        TableName tableName = TableName.valueOf("relation");

        Table table = connection.getTable(tableName);
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        delete.addColumn(Bytes.toBytes("follower"), Bytes.toBytes(followKey));
        System.out.println(rowKey + "取关了" + followKey);
        table.delete(delete);

        table.close();
    }

    /**
     * 删除粉丝
     * @param rowKey
     * @param fanKey
     * @throws IOException
     */
    public void delFans(String rowKey, String fanKey) throws IOException {
        TableName tableName = TableName.valueOf("relation");

        Table table = connection.getTable(tableName);
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        delete.addColumn(Bytes.toBytes("fans"), Bytes.toBytes(fanKey));
        System.out.println(fanKey + "不是" + rowKey + "的粉丝了");
        table.delete(delete);

        table.close();
    }

    /**
     * 通过rowKey查询用户信息返回昵称和密码
     * @param rowKey
     * @return 返回info[1]密码和info[0]昵称
     * @throws IOException
     */
    public User getUserInfo(String rowKey) throws IOException {
        TableName tableName = TableName.valueOf("user");
        User user = new User();

        Table table = connection.getTable(tableName);
        System.out.println("查询" + rowKey + "用户信息");
        Get get = new Get(Bytes.toBytes(rowKey));
        get.addFamily(Bytes.toBytes("info"));
        Result result = table.get(get);
        for (Cell cell : result.rawCells()) {
            System.out.println("\t行键:" + Bytes.toString(result.getRow()));
            System.out.println("\t列族" + Bytes.toString(CellUtil.cloneFamily(cell)));
            System.out.println("\t列:" + Bytes.toString(CellUtil.cloneQualifier(cell)));
            System.out.println("\t值:" + Bytes.toString(CellUtil.cloneValue(cell)));

            if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals("nickname")) {
                user.setNickname(Bytes.toString(CellUtil.cloneValue(cell)));
            } else if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals("password")) {
                user.setPassword(Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
        user.setRowKey(rowKey);
        table.close();

        return user;
    }

    /**
     * 获取所有用户
     * @return
     * @throws IOException
     */
    public List<User> getAllUser() throws IOException {
        TableName tableName = TableName.valueOf("user");
        List<User> userList = new ArrayList<User>();

        Table table = connection.getTable(tableName);
        System.out.println("查询所有用户信息");
        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes("info"));
        ResultScanner results = table.getScanner(scan);
        for (Result result : results) {
            User user = new User();
            user.setRowKey(Bytes.toString(result.getRow()));
            for (Cell cell : result.rawCells()) {
                if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals("nickname")) {
                    user.setNickname(Bytes.toString(CellUtil.cloneValue(cell)));
                } else if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals("password")) {
                    user.setPassword(Bytes.toString(CellUtil.cloneValue(cell)));
                }
            }
            userList.add(user);
        }
        table.close();

        return userList;
    }

    /**
     * 通过行键查询关注者
     * @param rowKey
     * @return 返回关注者字符串数组
     * @throws IOException
     */
    public List<User> getFollower(String rowKey) throws IOException {
        TableName tableName = TableName.valueOf("relation");
        List<User> userList = new ArrayList<User>();

        Table table = connection.getTable(tableName);
        System.out.println("查询" + rowKey + "关注者");
        Get get = new Get(Bytes.toBytes(rowKey));
        get.addFamily(Bytes.toBytes("follower"));
        Result result = table.get(get);
        for (Cell cell : result.rawCells()) {
            System.out.println("\t行键:" + Bytes.toString(result.getRow()));
            System.out.println("\t列族" + Bytes.toString(CellUtil.cloneFamily(cell)));
            System.out.println("\t列:" + Bytes.toString(CellUtil.cloneQualifier(cell)));

            User user = new User();
            user.setRowKey(Bytes.toString(CellUtil.cloneQualifier(cell)));
            userList.add(user);
        }
        table.close();

        return userList;
    }

    /**
     * 通过rowKey查询粉丝
     * @param rowKey
     * @return 返回粉丝字符串数组
     * @throws IOException
     */
    public List<User> getFans(String rowKey) throws IOException {
        TableName tableName = TableName.valueOf("relation");
        List<User> userList = new ArrayList<User>();

        Table table = connection.getTable(tableName);
        System.out.println("查询" + rowKey + "粉丝");
        Get get = new Get(Bytes.toBytes(rowKey));
        get.addFamily(Bytes.toBytes("fans"));
        Result result = table.get(get);
        for (Cell cell : result.rawCells()) {
            System.out.println("\t行键:" + Bytes.toString(result.getRow()));
            System.out.println("\t列族" + Bytes.toString(CellUtil.cloneFamily(cell)));
            System.out.println("\t列:" + Bytes.toString(CellUtil.cloneQualifier(cell)));

            User user = new User();
            user.setRowKey(Bytes.toString(CellUtil.cloneQualifier(cell)));
            userList.add(user);
        }
        table.close();

        return userList;
    }

    /**
     *
     * @param rowKey
     * @return 粉丝数
     * @throws IOException
     */
    public int getFanNum(String rowKey) throws IOException {
        TableName tableName = TableName.valueOf("relation");
        int num = 0;

        Table table = connection.getTable(tableName);
        Get get = new Get(Bytes.toBytes(rowKey));
        get.addFamily(Bytes.toBytes("fans"));
        Result result = table.get(get);
        for (Cell cell : result.rawCells()) {
            num++;
        }
        System.out.println(num + "个粉丝");

        return num;
    }

    /**
     *
     * @param rowKey
     * @return 关注数
     * @throws IOException
     */
    public int getFollowNum(String rowKey) throws IOException {
        TableName tableName = TableName.valueOf("relation");
        int num = 0;

        Table table = connection.getTable(tableName);
        Get get = new Get(Bytes.toBytes(rowKey));
        get.addFamily(Bytes.toBytes("follower"));
        Result result = table.get(get);
        for (Cell cell : result.rawCells()) {
            num++;
        }
        System.out.println(num + "个关注");

        return num;
    }


    public static void addUser(int num) throws IOException {
        Configuration configuration = null;
        Connection connection = null;

        System.out.println("准备连接");
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        configuration.set("hbase.zookeeper.quorum", "192.168.119.128");
        try {
            connection = ConnectionFactory.createConnection(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TableName tableName = TableName.valueOf("user");
        Table table = connection.getTable(tableName);

        for (int i = 3; i <= num; i++) {
            String rowKey = String.format("%04d", i);
            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("password"), Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("nickname"), Bytes.toBytes("alice"+i));
            System.out.println(rowKey + "\n" + "alice"+i);
            table.put(put);
        }
        table.close();
    }

    public static void main(String[] args) throws IOException {
        addUser(2000);
    }
}
