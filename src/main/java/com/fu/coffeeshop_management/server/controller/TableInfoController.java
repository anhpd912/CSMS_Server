package com.fu.coffeeshop_management.server.controller;

import com.fu.coffeeshop_management.server.dto.TableInfoDTO; // Import TableInfoDTO
import com.fu.coffeeshop_management.server.service.TableInfoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@CrossOrigin
public class TableInfoController {
    private final TableInfoService service;

    public TableInfoController(TableInfoService service) {
        this.service = service;
    }

    @GetMapping
    public List<TableInfoDTO> listTables( // Changed return type to List<TableInfoDTO>
            @RequestParam(required = false) String status,
            @RequestParam(required = false, name = "keyword") String keyword
    ) {
        return service.listTables(status, keyword);
    }
}
