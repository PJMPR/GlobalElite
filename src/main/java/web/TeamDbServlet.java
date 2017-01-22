package web;

import domain.model.Team;
import domain.model.TeamStatistics;
import hdao.TeamService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

/**
 * @author L on 13.01.2017.
 */
@WebServlet("/TeamDbServlet")
@Transactional
@Component
public class TeamDbServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session = req.getSession();
        Team team = (Team) session.getAttribute("team");
        TeamStatistics teamStatistics = (TeamStatistics) session.getAttribute("teamStats");
        List<Team> list = TeamService.getListOfTeam();

        if (team.getName() == null || team.getCountry() == null) {
            resp.getWriter().write("Nie wprowadziłeś wszystkich danych");
        }
        if (list.isEmpty()) {
            addTeam(team, teamStatistics, resp);
        }
        if (TeamService.getTeamByName(team.getName()) != null) {
            resp.getWriter().write("Podany team o danej nazwie juz istnieje.");
        } else {
            addTeam(team, teamStatistics, resp);
        }

    }


    private void addTeam(Team team, TeamStatistics teamStatistics, HttpServletResponse resp) throws ServletException, IOException {
        SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        Session session1 = sessionFactory.openSession();
        session1.beginTransaction();
        session1.save(teamStatistics);
        session1.save(team);
        session1.getTransaction().commit();
        session1.close();
        resp.sendRedirect("/index.html");
    }
}
