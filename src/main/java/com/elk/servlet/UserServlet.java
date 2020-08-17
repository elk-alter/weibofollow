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

@WebServlet(loadOnStartup = 1, urlPatterns = "/UserServlet")
public class UserServlet extends HttpServlet {

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
        System.out.println("UserServlet");
        resp.setContentType("application/text; charset=utf-8");

        HttpSession session = req.getSession();
        String status;
        String rowKey = req.getParameter("rowKey");
        User user = (User) session.getAttribute("User");
        int fan = hBaseUtil.getFanNum(rowKey);
        int follow = hBaseUtil.getFollowNum(rowKey);

        if (rowKey.equals(user.getRowKey())) {
            req.setAttribute("nickname", user.getNickname());
            req.setAttribute("username", user.getRowKey());
            req.setAttribute("fan", fan);
            req.setAttribute("follow", follow);
            req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req,resp);
        } else {
            boolean b = hBaseUtil.isFollow(user.getRowKey(), rowKey);
            System.out.println(b);
            String nickname = hBaseUtil.getUserInfo(rowKey).getNickname();
            System.out.println(nickname);
            if (b) {
                status = "#true {display: none;}#false {display: inline;}";
            } else {
                status = "#true {display: inline;}#false {display: none;}";
            }

            System.out.println(status);
            req.setAttribute("fan", fan);
            req.setAttribute("follow", follow);
            req.setAttribute("nickname", nickname);
            req.setAttribute("username", rowKey);
            req.setAttribute("status", status);
            req.getRequestDispatcher("/WEB-INF/jsp/user.jsp").forward(req,resp);
        }
    }
}
