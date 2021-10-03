package pt.tecnico.hds.mad.server;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.sqlite.*;
import pt.tecnico.hds.mad.lib.contract.Proof;
import pt.tecnico.hds.mad.lib.contract.Record;
import pt.tecnico.hds.mad.lib.contract.RecordProofPair;
import pt.tecnico.hds.mad.lib.contract.Report;
import pt.tecnico.hds.mad.server.exceptions.*;


public class Database {
    private final String DB_URL_PREFIX = "jdbc:sqlite:";
    private String databaseUrl;

    public Database(String filename) throws DatabaseCreationException {
        try {
            this.databaseUrl = DB_URL_PREFIX + filename;

            // Create database file if it doesn't exist.
            (new File(filename)).createNewFile();

            this.setSchema();

        } catch (IOException e) {
            e.printStackTrace();
            throw new DatabaseCreationException();
        }
    }

    private void setSchema() throws DatabaseCreationException {
        String record = String.join("",
            "CREATE TABLE IF NOT EXISTS record(",
                "user_id INT NOT NULL, ",
                "epoch INT NOT NULL, ",
                "x INT NOT NULL, ",
                "y INT NOT NULL, ",
                "PRIMARY KEY(user_id, epoch)",
            ")"
        );

        String proof = String.join("",
            "CREATE TABLE IF NOT EXISTS proof(",
                "user_id INT NOT NULL, ", // id of subject user (the one that issued the proof)
                "epoch INT NOT NULL, ", // epoch of issuance
                "signer_id INT  NOT NULL, ", // id of user that signed the record
                "signature TEXT NOT NULL, ", // maybe VARCHAR(size)? signatures have fixed length
                "PRIMARY KEY(user_id, epoch, signer_id), ",
                "FOREIGN KEY(user_id, epoch) REFERENCES record(user_id, epoch)",
            ")"
        );

        try (Connection connection = DriverManager.getConnection(this.databaseUrl)) {
            try {
                connection.setAutoCommit(false);
                Statement statement = connection.createStatement();
                statement.executeUpdate(record);
                statement.executeUpdate(proof);
                connection.commit();
            } catch(SQLException e) {
                connection.rollback();
            }
        } catch(SQLException e) {
            System.err.println("[DEBUG] SQL EXCEPTION");
            e.printStackTrace();
            throw new DatabaseCreationException();
        }
    }

    public void setReport(Report report) throws DatabaseDuplicateRecordException, DatabaseAccessException {
        try(Connection connection = DriverManager.getConnection(this.databaseUrl)) {
            try {
                connection.setAutoCommit(false);
                this.setRecord(connection, report.getRecord());

                for(Proof p : report.getProofs()) {
                    this.setProof(connection, report.getRecord(), p);
                }
                connection.commit();
            } catch(SQLException e) {
                connection.rollback();
            }
        } catch(SQLException e) {
            throw new DatabaseAccessException();
        }
    }

    public void setRecord(Record record) throws DatabaseDuplicateRecordException, DatabaseAccessException {
        try(Connection con = DriverManager.getConnection(this.databaseUrl)) {
            this.setRecord(con, record);
        } catch(SQLException e) {
            throw new DatabaseAccessException();
        }
    }
    private void setRecord(Connection connection, Record record)
            throws DatabaseDuplicateRecordException, DatabaseAccessException {
        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO record (user_id, epoch, x, y) VALUES(?, ?, ?, ?)")) {
            preparedStatement.setString(1, record.getUserId());
            preparedStatement.setInt(2, record.getEpoch());
            preparedStatement.setInt(3, record.getX());
            preparedStatement.setInt(4, record.getY());
            preparedStatement.executeUpdate();

        } catch (SQLiteException e) {
            if(e.getResultCode().equals(SQLiteErrorCode.SQLITE_CONSTRAINT_PRIMARYKEY)) {
                throw new DatabaseDuplicateRecordException();
            }
            throw new DatabaseAccessException();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseAccessException();
        }
    }

    private void setProof(Connection connection, Record record, Proof proof) throws DatabaseAccessException {
        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO proof (user_id, epoch, signer_id, signature) VALUES(?, ?, ?, ?)")) {

            preparedStatement.setString(1, record.getUserId());
            preparedStatement.setInt(2, record.getEpoch());
            preparedStatement.setString(3, proof.getSignerId());
            preparedStatement.setString(4, proof.getSignature());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[DEBUG] SQL EXCEPTION");
            e.printStackTrace();
            throw new DatabaseAccessException();
        }
    }

    public List<Record> getRecords() throws DatabaseAccessException {
        try(Connection connection = DriverManager.getConnection(this.databaseUrl)) {
            ArrayList<Record> list = new ArrayList<>();

            try(Statement statement = connection.createStatement();) {
                ResultSet rs = statement.executeQuery("SELECT * FROM RECORD");

                while (rs.next()) {
                    list.add(
                        new Record(
                            rs.getString("user_id"),
                            rs.getInt("epoch"),
                            rs.getInt("x"),
                            rs.getInt("y")
                        )
                    );
                }

            } catch (SQLException e) {
                System.err.println("[DEBUG] SQL EXCEPTION");
                e.printStackTrace();
                throw new DatabaseAccessException();
            }

            return list;
        } catch(SQLException e) { /* fall to default: throw exception */ }
        throw new DatabaseAccessException();
    }

    public Record getRecord(String userId, int epoch) throws
        DatabaseAccessException, 
        DatabaseNotFoundException,
        DatabaseTooManyResultsException 
    {
        try(Connection connection = DriverManager.getConnection(this.databaseUrl)) {
            try(PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM record WHERE user_id=? AND epoch=?")) {

                ps.setString(1, userId);
                ps.setInt(2, epoch);
                ResultSet rs = ps.executeQuery();

                if(!rs.next()) {
                    throw new DatabaseNotFoundException();
                }

                Record result = new Record(
                    rs.getString("user_id"),
                    rs.getInt("epoch"),
                    rs.getInt("x"),
                    rs.getInt("y"));

                if(rs.next()) {
                    throw new DatabaseTooManyResultsException();
                }

                return result;
            } catch (SQLException e) {
                System.err.println("[DEBUG] SQL EXCEPTION");
                e.printStackTrace();
                throw new DatabaseAccessException();
            }

        } catch(SQLException e) { /* fall to default: throw exception */ }
        throw new DatabaseAccessException();
    }

    public List<Record> getRecords(int x, int y, int epoch) throws
            DatabaseAccessException
    {
        try(Connection connection = DriverManager.getConnection(this.databaseUrl)) {
            ArrayList<Record> list = new ArrayList<>();

            try(PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM record WHERE x=? AND y=? AND epoch=?")) {

                statement.setInt(1, x);
                statement.setInt(2, y);
                statement.setInt(3, epoch);

                ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    list.add(new Record(rs.getString("user_id"),
                            rs.getInt("epoch"),
                            rs.getInt("x"),
                            rs.getInt("y")));
                }

            } catch (SQLException e) {
                System.err.println("[DEBUG] SQL EXCEPTION");
                e.printStackTrace();
                throw new DatabaseAccessException();
            }

            return list;
        } catch(SQLException e) { /* fall to default: throw exception */ }
        throw new DatabaseAccessException();
    }

    public List<Proof> getProofs(String userId, int epoch) throws DatabaseAccessException {
        try(Connection connection = DriverManager.getConnection(this.databaseUrl)) {
            ArrayList<Proof> list = new ArrayList<>();

            try(PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM proof WHERE user_id=? AND epoch=?")) {
                ps.setString(1, userId);
                ps.setInt(2, epoch);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    list.add(new Proof(
                            rs.getString("signer_id"),
                            rs.getString("signature")
                        ));
                }

            } catch (SQLException e) {
                System.err.println("[DEBUG] SQL EXCEPTION");
                e.printStackTrace();
                throw new DatabaseAccessException();
            }

            return list;
        } catch(SQLException e) { /* fall to default: throw exception */ }
        throw new DatabaseAccessException();
    }

    public Proof getProof(String userId, int epoch, String signerId) throws
            DatabaseAccessException,
            DatabaseNotFoundException,
            DatabaseTooManyResultsException {
        try(Connection connection = DriverManager.getConnection(this.databaseUrl)) {
            try(PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM proof WHERE signer_id=? AND epoch=? AND user_id=?")) {

                ps.setString(1, signerId);
                ps.setInt(2, epoch);
                ps.setString(3, userId);
                ResultSet rs = ps.executeQuery();

                if(!rs.next()) {
                    throw new DatabaseNotFoundException();
                }

                Proof result = new Proof(
                        rs.getString("signer_id"),
                        rs.getString("signature"));

                if(rs.next()) {
                    throw new DatabaseTooManyResultsException();
                }

                return result;

            } catch (SQLException e) {
                System.err.println("[DEBUG] SQL EXCEPTION");
                e.printStackTrace();
                throw new DatabaseAccessException();
            }
        } catch (SQLException e) { }
        throw new DatabaseAccessException();
    }

    public List<RecordProofPair> getProofsOfUser(String signerId, int epoch)
        throws DatabaseAccessException, DatabaseNotFoundException {

        try(Connection connection = DriverManager.getConnection(this.databaseUrl)) {
            ArrayList<RecordProofPair> list = new ArrayList<>();

            try(PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM proof WHERE signer_id=? AND epoch=? AND user_id!=signer_id")) {
                ps.setString(1, signerId);
                ps.setInt(2, epoch);
                ResultSet rs = ps.executeQuery();

                int numRows = 0;

                while(rs.next()) {
                    Record correspondingRecord = getRecord(rs.getString("user_id"), epoch);
                    Proof proof = new Proof(
                            rs.getString("signer_id"),
                            rs.getString("signature")
                    );
                    list.add(new RecordProofPair(correspondingRecord, proof));
                    numRows++;
                }

                if (numRows == 0) {
                    throw new DatabaseNotFoundException();
                }
            } catch (SQLException | DatabaseTooManyResultsException e) {
                System.err.println("[DEBUG] SQL EXCEPTION");
                e.printStackTrace();
                throw new DatabaseAccessException();
            }

            return list;

        } catch(SQLException e) { }
        throw new DatabaseAccessException();
    }
}
