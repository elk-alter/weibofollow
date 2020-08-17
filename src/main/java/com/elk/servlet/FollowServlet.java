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

@WebServlet(loadOnStartup = 1, urlPatterns = "/FollowServlet")
public class FollowServlet extends HttpServlet {
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
        String rowKey = req.getParameter("rowKey");
        System.out.println(id + rowKey);
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("User");
        User user1 = hBaseUtil.getUserInfo(rowKey);
        int fan;
        int follow;
        String status;

        System.out.println("FollowServlet：id="+ id);

        //关注
        if (id.equals("true")) {
            System.out.println("关注");
            hBaseUtil.addFollower(user.getRowKey(), rowKey);
            hBaseUtil.addFans(rowKey, user.getRowKey());

            fan = hBaseUtil.getFanNum(rowKey);
            follow = hBaseUtil.getFollowNum(rowKey);
            status = "#true {display: none;}#false {display: inline;}";

            req.setAttribute("fan", fan);
            req.setAttribute("follow", follow);
            req.setAttribute("nickname", user1.getNickname());
            req.setAttribute("username", rowKey);
            req.setAttribute("status", status);
            req.getRequestDispatcher("/WEB-INF/jsp/user.jsp").forward(req,resp);
        }
        //取关
        if (id.equals("false")) {
            System.out.println("取关");
            hBaseUtil.delFollower(user.getRowKey(), rowKey);
            hBaseUtil.delFans(rowKey, user.getRowKey());

            fan = hBaseUtil.getFanNum(rowKey);
            follow = hBaseUtil.getFollowNum(rowKey);
            status = "#true {display: inline;}#false {display: none;}";

            req.setAttribute("fan", fan);
            req.setAttribute("follow", follow);
            req.setAttribute("nickname", user1.getNickname());
            req.setAttribute("username", rowKey);
            req.setAttribute("status", status);
            req.getRequestDispatcher("/WEB-INF/jsp/user.jsp").forward(req,resp);
        }
        //回主页
        if (id.equals("home")) {
            System.out.println("gohome");
            req.setAttribute("nickname", user.getNickname());
            req.setAttribute("username", user.getRowKey());

            fan = hBaseUtil.getFanNum(user.getRowKey());
            follow = hBaseUtil.getFollowNum(user.getRowKey());

            req.setAttribute("fan", fan);
            req.setAttribute("follow", follow);
            req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req,resp);
        }
    }
}
