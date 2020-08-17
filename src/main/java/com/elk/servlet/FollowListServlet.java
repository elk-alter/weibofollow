package com.elk.servlet;

import com.elk.bean.User;
import com.elk.util.HBaseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(loadOnStartup = 1, urlPatterns = "/FollowListServlet")
public class FollowListServlet extends HttpServlet {

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

        String rowKey = req.getParameter("rowKey");
        System.out.println(rowKey);

        List<User> userList = hBaseUtil.getFollower(rowKey);
        for (User user: userList) {
            String nickname = hBaseUtil.getUserInfo(user.getRowKey()).getNickname();
            user.setNickname(nickname);
        }

        req.setAttribute("followlist", userList);
        req.getRequestDispatcher("/WEB-INF/jsp/follow.jsp").forward(req,resp);
    }
}
