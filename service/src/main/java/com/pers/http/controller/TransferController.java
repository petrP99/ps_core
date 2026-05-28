package com.pers.http.controller;

import com.pers.dto.TransferCreateDto;
import com.pers.dto.TransferReadDto;
import com.pers.dto.filter.PageResponse;
import com.pers.dto.filter.TransferFilterDto;
import com.pers.service.CardService;
import com.pers.service.ClientService;
import com.pers.service.TransferService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.pers.enums.Status.ACTIVE;
import static org.springframework.http.HttpStatus.NOT_FOUND;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/transfers")
public class TransferController {

    private final TransferService transferService;
    private final CardService cardService;
    private final ClientService clientService;

    @GetMapping("/transfer-between")
    public String transferBetweenClientCards(@Validated @ModelAttribute("transfer") TransferCreateDto transfer, Model model, HttpSession session) {
        var cards = cardService.findActiveCardsByClientId((Long) session.getAttribute("clientId"));
        model.addAttribute("cards", cards);
        model.addAttribute("transfer", transfer);
        return "transfer/transfer-between";
    }

    @GetMapping("/transfer")
    public String transfer(@Validated @ModelAttribute("transfer") TransferCreateDto transfer, Model model, HttpSession session) {
        var cards = cardService.findActiveCardsAndPositiveBalanceByClientId((Long) session.getAttribute("clientId"));
        model.addAttribute("cards", cards);
        model.addAttribute("transfer", transfer);
        return "transfer/transfer";
    }

    @GetMapping("/transfer-phone")
    public String transferByPhone(@Validated @ModelAttribute("transfer") TransferCreateDto transfer, Model model, HttpSession session) {
        var cards = cardService.findActiveCardsAndPositiveBalanceByClientId((Long) session.getAttribute("clientId"));
        model.addAttribute("cards", cards);
        model.addAttribute("transfer", transfer);
        return "transfer/transfer-phone";
    }

    @GetMapping("/check")
    public String checkTransfer(@Validated TransferCreateDto transfer, HttpSession session, Model model) {
        var recipient = clientService.findFirstAndLastNameByClientId(cardService.findById(transfer.getCardIdTo()).orElseThrow().clientId());
        session.setAttribute("transfer", transfer);
        model.addAttribute("recipient", recipient);
        return "transfer/check";
    }

    @GetMapping("/check-phone")
    public String checkTransferByPhone(@RequestParam("phone") String phone, @Validated TransferCreateDto transfer, Model model, HttpSession session) {
        var clientTo = clientService.findByPhone(phone).orElseThrow().getId();
        var cardTo = cardService.findByClientId(clientTo).stream()
                .filter(it -> it.status().equals(ACTIVE))
                .findAny()
                .orElseThrow().id();

        var recipient = clientService.findFirstAndLastNameByClientId(clientTo);
        var updateTransfer = TransferCreateDto.builder()
                .clientId(transfer.getClientId())
                .cardIdFrom(transfer.getCardIdFrom())
                .cardIdTo(cardTo)
                .amount(transfer.getAmount())
                .time(transfer.getTime())
                .recipient(recipient)
                .message(transfer.getMessage())
                .status(transfer.getStatus())
                .build();

        model.addAttribute("recipient", recipient);
        session.setAttribute("transfer", updateTransfer);
        return "transfer/check";
    }

    @PostMapping("/create-phone")
    public String createByPhone(@Validated TransferCreateDto transfer, RedirectAttributes redirectAttributes, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("transfer", transfer);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/transfers";
        }
        return transferService.checkAndCreateTransfer(transfer) ? "transfer/success" : "transfer/fail";
    }

    @PostMapping("/create-between")
    public String createBetween(@Validated TransferCreateDto transfer, RedirectAttributes redirectAttributes, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("transfer", transfer);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/transfers";
        }
        return transferService.checkAndCreateTransfer(transfer) ? "transfer/success" : "transfer/fail";
    }

    @PostMapping("/create")
    public String create(@Validated TransferCreateDto transfer, HttpSession session, RedirectAttributes redirectAttributes, BindingResult bindingResult) {
        transfer = (TransferCreateDto) session.getAttribute("transfer");
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("transfer", transfer);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/transfers";
        }
        return transferService.checkAndCreateTransfer(transfer) ? "transfer/success" : "transfer/fail";
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public String delete(@PathVariable("id") Long id) {
        if (!transferService.delete(id)) {
            throw new ResponseStatusException(NOT_FOUND);
        }
        return "redirect:/transfers";
    }

    @GetMapping("/clientTransfers")
    public String findAllByClientByFilter(Model model, TransferFilterDto filter, Pageable pageable, HttpSession session) {
        Page<TransferReadDto> page = transferService.findAllByClientByFilter(filter, pageable, (Long) session.getAttribute("clientId"));
        model.addAttribute("transfers", PageResponse.of(page));
        model.addAttribute("filter", filter);
        return "transfer/clientTransfers";
    }

    @GetMapping("/transfers")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public String findAll(Model model, TransferFilterDto filter, Pageable pageable) {
        Page<TransferReadDto> page = transferService.findAllByFilter(filter, pageable);
        model.addAttribute("transfers", PageResponse.of(page));
        model.addAttribute("filter", filter);
        return "transfer/transfers";
    }

}
