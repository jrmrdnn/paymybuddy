package com.app.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TransferController {

  @GetMapping("/transfer")
  public String showTransferPage(RedirectAttributes redirectAttributes) {
    return "transfer";
  }
}
