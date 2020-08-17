package com.elk.servlet;

import com.elk.bean.User;
import com.elk.util.HBaseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(loadOnStartup = 1, urlPatterns = "/LoginServlet")
public class LoginServlet extends HttpServlet {
    HBaseUtil hBaseUtil = null;

    @Override
    public void init() throws ServletException {
        hBaseUtil = new HBaseUtil();
        hBaseUtil.getCon();
        System.out.println("init success");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Servlet");
        resp.setContentType("application/text; charset=utf-8");

        String id = req.getParameter("id");
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        User user = hBaseUtil.getUserInfo(username);

        HttpSession session = req.getSession();
        session.setAttribute("User", user);

        int fan = hBaseUtil.getFanNum(username);
        int follow = hBaseUtil.getFollowNum(username);

        if (user.getPassword().equals(password)) {
            System.out.println("登录成功");
            req.setAttribute("nickname", user.getNickname());
            req.setAttribute("username", username);
            req.setAttribute("fan", fan);
            req.setAttribute("follow", follow);
            req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req,resp);
        }
    }
}
