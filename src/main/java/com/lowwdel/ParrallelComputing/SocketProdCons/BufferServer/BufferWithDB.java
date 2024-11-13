package com.lowwdel.ParrallelComputing.SocketProdCons.BufferServer;

import com.lowwdel.ParrallelComputing.SocketProdCons.utils.DatabaseUtil;

import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BufferWithDB {
    private static final int CLEANUP_THRESHOLD = 1000;
    private final ExecutorService maintenanceExecutor;
    private final BufferMaintenanceTask maintenanceTask;
    private boolean resetFlag = true;

    public BufferWithDB(){
        this.maintenanceExecutor = Executors.newSingleThreadExecutor();
        this.maintenanceTask = new BufferMaintenanceTask();
        this.maintenanceExecutor.submit(maintenanceTask);
    }
    public synchronized void put(Long value){
        try(Connection conn = DatabaseUtil.getConnection()){
            String sql ="INSERT INTO messages (value) VALUES (?)";
            try(PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setLong(1,value);
                stmt.executeUpdate();
                System.out.println("数据已插入");
            }
        }catch (SQLException e){
            throw new RuntimeException("插入数据失败："+e.getMessage());
        }
    }
    public synchronized Long take(){
        //取出代码不是物理取出，是将数据的status设为1（已取出）
        Long value = null;
        try (Connection conn = DatabaseUtil.getConnection()){
            String selectSql = "SELECT id,value FROM messages WHERE status = 0 ORDER BY id LIMIT 1";
            String updateSql = "UPDATE messages SET status = 1 WHERE id = ?";
            try(PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                PreparedStatement updateStmt = conn.prepareStatement(updateSql)){

                //检索要出队的数据，即第一个status为0的数据
                ResultSet rs = selectStmt.executeQuery();
                if(rs.next()){
                    int id = rs.getInt("id");
                    value = rs.getLong("value");

                    updateStmt.setInt(1,id);
                    updateStmt.executeUpdate();
                    System.out.println("数据已消费");
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("获取数据失败"+e.getMessage());
        }
        return value;
    }

    private class BufferMaintenanceTask implements Runnable{
        @Override
        public void run(){
            while(true){
                try {
                    Thread.sleep(2000);
                    cleanUpDatabase();
                } catch (InterruptedException e) {
                    System.err.println("维护线程被中断"+e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                }

            }
        }

        private void cleanUpDatabase(){
            try(Connection conn = DatabaseUtil.getConnection()){
                System.out.println("执行批量处理");

                //判断是否可重置自增ID（首先得有数据可被清理再重置）如果数据库为空则清理不了
                if(!isDatabaseEmpty(conn)){
                    resetFlag = true;
                }

                //清理已消费的数据
                cleanupConsumedData(conn);

                //如果数据库为空则重置自增ID
                if(isDatabaseEmpty(conn) && resetFlag){
                    resetAutoIncrement(conn);
                    resetFlag = false;
                }


            }catch (SQLException e){
                System.out.println("清理任务失败" + e.getMessage());
            }
        }
        private void cleanupConsumedData(Connection conn) throws SQLException{
            String deleteSql = "DELETE FROM messages WHERE status = 1";
            try(PreparedStatement stmt = conn.prepareStatement(deleteSql)){
                int rowsDeleted = stmt.executeUpdate();
                if(rowsDeleted > 0){
                    System.out.println("已清理"+rowsDeleted+"条数据");
                }
            }
        }
        //数据库为空：表中没有任何数据 or 表中所有数据都已被消费（即查到的数据status全为1）
        private boolean isDatabaseEmpty(Connection conn) throws SQLException{
            String countSql = "SELECT COUNT(*) FROM messages";
            try(Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(countSql)){
                //如果有数据则条件判断数据是否被消费
                if(rs.next()){
                    int count = rs.getInt(1);
                    return count == 0;
                }
            }
            return true;
        }
        private void resetAutoIncrement(Connection conn) throws SQLException{
            String resetAutoIncrementSql = "ALTER TABLE messages AUTO_INCREMENT = 1";;
            try(Statement stmt = conn.createStatement()){
                stmt.execute(resetAutoIncrementSql);
                System.out.println("自增ID已重置");
            }
        }
    }
}
