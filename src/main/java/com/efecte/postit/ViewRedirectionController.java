package com.efecte.postit;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * A view controller implementing the ErrorController interface
 */
@Controller
public class ViewRedirectionController implements ErrorController {

    /** Handle HTTP error codes (4xx and 5xx) <br />
     * If currently handled code is <code>404</code>, forward to <code>index.html</code>
     * @return View informing of the encountered error or forward to <code>index.html</code>
     */
    @GetMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status == null) return "redirect:/failure.html";

        int statusCode = Integer.parseInt(status.toString());
        if (statusCode == HttpStatus.NOT_FOUND.value()) return "forward:/";

        return String.format("redirect:/%s.html", statusCode);
    }

}

