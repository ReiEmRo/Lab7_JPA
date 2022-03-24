package ca.sait.lab7.servlets;

import ca.sait.lab7.models.Role;
import ca.sait.lab7.models.User;
import ca.sait.lab7.services.RoleService;
import ca.sait.lab7.services.UserService;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ReiEm
 */
public class UserServlet extends HttpServlet {

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserService uService = new UserService();
        RoleService rService = new RoleService();

        String action = request.getParameter("action");

        if (action != null && action.equals("delete")) {
            try {
                String email = request.getParameter("email");
                boolean deleted = uService.delete(email);

            } catch (Exception ex) {
                Logger.getLogger(UserServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            List<User> users = uService.getAll();
            List<Role> roles = rService.getAll();

            request.setAttribute("users", users);
            request.setAttribute("roles", roles);

            this.getServletContext().getRequestDispatcher("/WEB-INF/users.jsp").forward(request, response);
        } catch (Exception ex) {
            Logger.getLogger(UserServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserService uService = new UserService();

        String btnPressed = request.getParameter("btn");

        String email;
        boolean active = true;
        String firstName;
        String lastName;
        String password;
        int roleId;
        String roleName = null;
        Role role;

        switch (btnPressed) {
            case "add":
                email = request.getParameter("new-email");
                firstName = request.getParameter("new-fname");
                lastName = request.getParameter("new-lname");
                password = request.getParameter("new-pass");
                roleId = Integer.parseInt(request.getParameter("new-role"));
                switch (roleId) {
                    case 1:
                        roleName = "system admin";
                        break;
                    case 2:
                        roleName = "regular user";
                        break;
                    case 3:
                        roleName = "company admin";
                        break;
                }

                role = new Role(roleId, roleName);
                try {
                    uService.insert(email, active, firstName, lastName, password, role);
                } catch (Exception ex) {
                    request.setAttribute("message", "Duplicate email detected, please try again.");
                }
                break;

            case "save":
                try {
                email = request.getParameter("email-to-edit");
                User user = uService.get(email);
                if (request.getParameter("edited-role").equals("")) {
                    role = user.getRole();
                } else {
                    roleId = Integer.parseInt(request.getParameter("edited-role"));
                    switch (roleId) {
                        case 1:
                            roleName = "system admin";
                            break;
                        case 2:
                            roleName = "regular user";
                            break;
                        case 3:
                            roleName = "company admin";
                            break;
                    }

                    role = new Role(roleId, roleName);
                }

                if (user.isActive()) {
                    firstName = request.getParameter("edited-fname");
                    firstName = (firstName.equals("")) ? user.getFirstName() : firstName;
                    lastName = request.getParameter("edited-lname");
                    lastName = (lastName.equals("")) ? user.getLastName() : lastName;

                    uService.update(email, active, firstName, lastName, user.getPassword(), role);
                } else {
                    request.setAttribute("message", "User is inactive, cannot be modified.");
                }
            } catch (Exception ex) {
                request.setAttribute("message", "User not found please try again.");
            }
            break;

            default:
                break;
        }

        doGet(request, response);
    }
}
