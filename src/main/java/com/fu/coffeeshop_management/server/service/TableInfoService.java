package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.entity.TableInfo;
import com.fu.coffeeshop_management.server.repository.TableInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableInfoService {
    private final TableInfoRepository tableRepo;

    public TableInfoService(TableInfoRepository tableRepo) {
        this.tableRepo = tableRepo;
    }

    public List<TableInfo> listTables(String status, String keyword) {

        boolean hasStatus = (status != null && !status.isBlank());
        boolean hasKeyword = (keyword != null && !keyword.isBlank());

        if (hasStatus && hasKeyword) {
            return tableRepo.findByStatusIgnoreCaseAndNameContainingIgnoreCase(status, keyword);
        }
        else if (hasStatus) {
            return tableRepo.findByStatusIgnoreCase(status);
        }
        else if (hasKeyword) {
            return tableRepo.findByNameContainingIgnoreCase(keyword);
        }
        else {
            return tableRepo.findAll();
        }
    }
}