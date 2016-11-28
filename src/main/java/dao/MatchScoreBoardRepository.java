package dao;

import dao.mappers.IMapResultSetIntoEntity;
import dao.repositories.IMatchScoreBoardRepository;
import dao.uow.IUnitOfWork;
import domain.model.MatchScoreBoard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author L on 13.11.2016.
 */
public class MatchScoreBoardRepository extends RepositoryBase<MatchScoreBoard> implements IMatchScoreBoardRepository {


    private PreparedStatement getKillsInMatch;
    private PreparedStatement getDeathsInMatch;

    public MatchScoreBoardRepository(Connection connection, IMapResultSetIntoEntity<MatchScoreBoard> mapper, IUnitOfWork uow) {
        super(connection, mapper, uow);

        try {
            getKillsInMatch = connection.prepareStatement(getKillsInMatchSql());
            getDeathsInMatch = connection.prepareStatement(getDeathsInMatchSql());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected String getKillsInMatchSql() {
        return "SELECT * FROM SCOREBOARD where killsInMatch=?";
    }

    protected String getDeathsInMatchSql() {
        return "SELECT * FROM SCOREBOARD where deathsInMatch=?";
    }

    @Override
    public List<MatchScoreBoard> withKillsInMatch(int killsInMatch) {
        return searchByInt(killsInMatch, getKillsInMatch);
    }

    @Override
    public List<MatchScoreBoard> withDeathsInMatch(int deathInMatch) {
        return searchByInt(deathInMatch, getDeathsInMatch);
    }

    @Override
    protected String insertSql() {
        return "INSERT INTO SCOREBOARD(killsInMatch, deathsInMatch, PLAYER_ID) values (?, ?, ?)";
    }

    @Override
    protected String updateSql() {
        return "UPDATE SCOREBOARD SET (killsInMatch, deathsInMatch, PLAYER_ID) = (?, ? , ?) where id=?";
    }

    @Override
    protected void setUpdate(MatchScoreBoard matchScoreBoard) throws SQLException {
        update.setInt(1, matchScoreBoard.getKillsInMatch());
        update.setInt(2, matchScoreBoard.getDeathsInMatch());
        update.setInt(3, matchScoreBoard.getPlayer().getId());
    }

    @Override
    protected void setInsert(MatchScoreBoard matchScoreBoard) throws SQLException {
        insert.setInt(1, matchScoreBoard.getKillsInMatch());
        insert.setInt(2, matchScoreBoard.getDeathsInMatch());
        insert.setInt(3, matchScoreBoard.getPlayer().getId());
    }

    @Override
    protected String createTableSql() {
        return "" + "CREATE TABLE SCOREBOARD("
                + "id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,"
                + "killsInMatch int,"
                + "deathsInMatch int,"
                + "PLAYER_ID int,"
                + "FOREIGN KEY (PLAYER_ID) REFERENCES PLAYER(id)" + ")";
    }

    @Override
    protected String tableName() {
        return "SCOREBOARD";
    }
}