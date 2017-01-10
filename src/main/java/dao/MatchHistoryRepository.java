package dao;

import dao.mappers.IMapResultSetIntoEntity;
import dao.repositories.IMatchHistoryRepository;
import dao.uow.IUnitOfWork;
import domain.model.MatchHistory;
import domain.model.MatchScoreBoard;

import java.sql.*;
import java.util.List;

/**
 * @author L on 13.11.2016.
 */
public class MatchHistoryRepository extends RepositoryBase<MatchHistory> implements IMatchHistoryRepository {


    private PreparedStatement getScores;
    private PreparedStatement getLastIdOfTeam1;
    private PreparedStatement getLastIdOfTeam2;



    public MatchHistoryRepository(Connection connection, IMapResultSetIntoEntity<MatchHistory> mapper, IUnitOfWork uow) {
        super(connection, mapper, uow);

        try {
            getScores = connection.prepareStatement(getScoresSql());
            getLastIdOfTeam1 = connection.prepareStatement(getLastIdOfTeam1Sql());
            getLastIdOfTeam2 = connection.prepareStatement(getLastIdOfTeam2Sql());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    protected String getLastIdOfTeam1Sql() {
        return "UPDATE HISTORY_OF_MATCH SET(TEAM_1_ID) = (SELECT max(id) from TEAM) where id = (SELECT max(id) FROM HISTORY_OF_MATCH)";
    }

    protected String getLastIdOfTeam2Sql() {
        return "UPDATE HISTORY_OF_MATCH SET(TEAM_2_ID) = (SELECT max(id) from TEAM) where id = (SELECT max(id) FROM HISTORY_OF_MATCH)";
    }

    protected String getLastIdOfMapSql() {
        return "UPDATE HISTORY_OF_MATCH SET(MAP_ID) = (SELECT max(id) from MAP) where id = (SELECT max(id) FROM HISTORY_OF_MATCH)";
    }

    protected String getLastIdOfScoreBoardSql() {
        return "UPDATE HISTORY_OF_MATCH SET(SCOREBOARD_ID) = (SELECT max(id) from SCOREBOARD) where id = (SELECT max(id) FROM HISTORY_OF_MATCH)";
    }


    protected String getScoresSql() {
        return "SELECT * FROM HISTORY_OF_MATCH where SCOREBOARD_ID=?";
    }


    @Override
    public List<MatchHistory> withScores(MatchScoreBoard matchScoreBoard) {
        return searchByInt(matchScoreBoard.getId(), getScores);
    }


    @Override
    protected String insertSql() {
        return "INSERT INTO HISTORY_OF_MATCH(scoreOfTeam1, scoreOfTeam2, timeOfMatch) values (?, ?, ?)";
    }

    @Override
    protected String updateSql() {
        return "UPDATE HISTORY_OF_MATCH SET (scoreOfTeam1, scoreOfTeam2, timeOfMatch, TEAM_1_ID, TEAM_2_ID" +
                ", MAP_ID, SCOREBOARD_ID)=(?,?,?,?,?,?,?) where id=?";
    }

    @Override
    protected void setUpdate(MatchHistory matchHistory) throws SQLException {
        update.setInt(1, matchHistory.getScoreOfTeam1());
        update.setInt(2, matchHistory.getScoreOfTeam2());
        update.setDate(3, (Date) matchHistory.getTimeOfMatch());
        update.setInt(4, matchHistory.getTeam1().getId());
        update.setInt(5, matchHistory.getTeam1().getId());
        update.setInt(6, matchHistory.getGameMap().getId());
        update.setInt(7, matchHistory.getMatchScoreBoard().getId());
    }

    @Override
    protected void setInsert(MatchHistory matchHistory) throws SQLException {
        insert.setInt(1, matchHistory.getScoreOfTeam1());
        insert.setInt(2, matchHistory.getScoreOfTeam2());
        insert.setDate(3, (Date) matchHistory.getTimeOfMatch());

        ResultSet resultSet = insert.getGeneratedKeys();
        if (resultSet.next()) {
            matchHistory.setId((resultSet.getInt(1)));
        }
    }

    @Override
    protected String createTableSql() {
        return "" + "CREATE TABLE HISTORY_OF_MATCH("
                + "id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,"
                + "scoreOfTeam1 int,"
                + "scoreOfTeam2 int,"
                + "timeOfMatch date,"
                + "TEAM_1_ID int,"
                + "TEAM_2_ID int,"
                + "MAP_ID int,"
                + "SCOREBOARD_ID int,"
                + "FOREIGN KEY (TEAM_1_ID) REFERENCES TEAM(id),"
                + "FOREIGN KEY (TEAM_2_ID) REFERENCES TEAM(id),"
                + "FOREIGN KEY (MAP_ID) REFERENCES MAP(id),"
                + "FOREIGN KEY (SCOREBOARD_ID) REFERENCES SCOREBOARD(id)"
                + ")";
    }

    @Override
    protected String tableName() {
        return "HISTORY_OF_MATCH";
    }
}
