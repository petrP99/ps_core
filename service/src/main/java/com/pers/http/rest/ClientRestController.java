package com.pers.http.rest;

import com.pers.dto.ClientCreateDto;
import com.pers.dto.ClientReadDto;
import com.pers.dto.filter.ClientFilterDto;
import com.pers.dto.filter.PageResponse;
import com.pers.service.ClientService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class ClientRestController {

    private final ClientService clientService;

    @PutMapping("/{id}")
    public ResponseEntity<ClientReadDto> update(@PathVariable("id") Long id, @RequestBody @Validated ClientCreateDto client) {
        return clientService.update(id, client)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    @PutMapping("/{id}/admin")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ClientReadDto> updateByAdmin(@PathVariable("id") Long id, @RequestBody @Validated ClientCreateDto client) {
        return clientService.update(id, client)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public void delete(@PathVariable("id") Long id) {
        if (!clientService.delete(id)) {
            throw new ResponseStatusException(NOT_FOUND);
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public PageResponse<ClientReadDto> findAll(ClientFilterDto filter, Pageable pageable) {
        Page<ClientReadDto> page = clientService.findAll(filter, pageable);
        return PageResponse.of(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ClientReadDto> findById(@PathVariable("id") Long id) {
        return clientService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

}
