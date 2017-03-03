import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    User user;
    static HashMap<String, User> users = new HashMap<>();

    public static void main(String[] args) {
        Spark.init();

        Spark.get("/",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("userName");//saves name into the session, each post session
                    //keeps track of the user

                    //users.get(name);
                    User user = users.get(name);
                    HashMap m = new HashMap();
                    if (user == null) {
                        return new ModelAndView(m, "index.html");
                    } else {
                        m.put("name", user.name);
                        m.put("messages", user.messages);
                        return new ModelAndView(m, "messages.html");
                    }
                },
                new MustacheTemplateEngine()
        );

        Spark.post("/create-user", (request, response) -> {

            String name = request.queryParams("loginName");
            String password = request.queryParams("password");
            User user = users.get(name);//gets the user object
            if (user == null) {
                Session session = request.session();
                user = new User(name, password);//creates user and establishes password for first time
                users.put(name, user);//adds user to the HashMap
                session.attribute("userName", name); //saves the username into this session for first time
                response.redirect("/");// back to the front
            }
            else if (user.password.equals(password)){//if user exists then checks valid password
                Session session = request.session();
                session.attribute("userName", name);//binds to the user session
                response.redirect("/");
            }
            else{
                response.redirect("/");
            }
            return "";

        });

        Spark.post("/messages", (request, response) -> {
            Session session = request.session();
            String name = session.attribute("userName");
            String msgs = request.queryParams("text");
            Message message = new Message(msgs);
            User user = users.get(name);
            user.messages.add(message);
            response.redirect("/");
            return "";
        });

        Spark.post("/logout", (request, response) -> {
            Session session = request.session();
            session.invalidate();
            response.redirect("/");
            return "";
        });

        Spark.post("/delete", (request, response) -> {
            Session session = request.session();
            String name = session.attribute("userName");
            User user = users.get(name);
            int number = Integer.parseInt(request.queryParams("number"));
            user.messages.remove(number - 1);
            response.redirect("/");
            return "";
        });

        Spark.post("/change", (request, response) -> {
            Session session = request.session();
            String name = session.attribute("userName");
            String msgs = request.queryParams("text");
            Message message = new Message(msgs);

            User user = users.get(name);
            int number = Integer.parseInt(request.queryParams("number"));
            user.messages.set(number - 1, message);

            response.redirect("/");
            return "";
        });


    }
}
