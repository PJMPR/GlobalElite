package dao;

import dao.mappers.IMapResultSetIntoEntity;
import dao.repositories.ITeamRepository;
import dao.uow.IUnitOfWork;
import domain.model.Player;
import domain.model.Team;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author L on 13.11.2016.
 */
public class TeamRepository extends RepositoryBase<Team> implements ITeamRepository {


    private PreparedStatement getName;
    private PreparedStatement getCountry;
    private PreparedStatement getPlayer;

    public TeamRepository(Connection connection, IMapResultSetIntoEntity<Team> mapper, IUnitOfWork uow) {
        super(connection, mapper, uow);

        try {
            getName = connection.prepareStatement(getNameSql());
            getCountry = connection.prepareStatement(getCountrySql());
            getPlayer = connection.prepareStatement(getPlayerSql());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    protected String getNameSql() {
        return "SELECT * FROM TEAM where name = ?";
    }

    protected String getCountrySql() {
        return "SELECT * FROM TEAM where country = ?";
    }

    protected String getPlayerSql() {
        return "SELECT * FROM TEAM where PLAYERS_ID  = ?";
    }

    protected String insertSql() {
        return "INSERT INTO TEAM(name, country, TEAM_STATS_ID, PLAYERS_ID) values (?, ?, ?, ?)";
    }

    protected String updateSql() {
        return "UPDATE TEAM SET (name, country, TEAM_STATS_ID, PLAYERS_ID) = (?,?,?,?) where id=?";
    }

    protected void setUpdate(Team team) throws SQLException {
        update.setString(1, team.getName());
        update.setString(2, team.getCountry());
        update.setInt(3, team.getGeneralTeamStats().getId());
        update.setInt(4, team.getPlayers().getId());
    }

    protected void setInsert(Team team) throws SQLException {
        insert.setString(1, team.getName());
        insert.setString(2, team.getCountry());
        insert.setInt(3, team.getGeneralTeamStats().getId());
        insert.setInt(4, team.getPlayers().getId());
    }


    @Override
    public List<Team> withName(String name) {
        return searchByString(name, getName);
    }

    @Override
    public List<Team> withCountry(String country) {
        return searchByString(country, getCountry);
    }

    @Override
    public List<Team> withPlayer(Player player) {
        List<Team> playerInTeam = new ArrayList<>();
        try {
            getPlayer.setObject(1, player);
            ResultSet resultSet = getPlayer.executeQuery();
            while (resultSet.next()) {
                playerInTeam.add(mapper.map(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerInTeam;
    }

    @Override
    protected String createTableSql() {
        return "" + "CREATE TABLE TEAM("
                + "id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,"
                + "name varchar(25),"
                + "country varchar(25),"
                + "TEAM_STATS_ID int,"
                + "PLAYERS_ID int,"
                + "FOREIGN KEY (TEAM_STATS_ID) REFERENCES TEAM_STATS(id),"
                + "FOREIGN KEY (PLAYERS_ID) REFERENCES PLAYER(id)" + ")";
    }

    @Override
    protected String tableName() {
        return "TEAM";
    }
}
