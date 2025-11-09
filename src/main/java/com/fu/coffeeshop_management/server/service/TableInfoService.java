package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.TableInfoDTO; // Import TableInfoDTO
import com.fu.coffeeshop_management.server.entity.TableInfo;
import com.fu.coffeeshop_management.server.repository.TableInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors; // Import Collectors

@Service
public class TableInfoService {
    private final TableInfoRepository tableRepo;

    public TableInfoService(TableInfoRepository tableRepo) {
        this.tableRepo = tableRepo;
    }

    // Helper method to convert TableInfo entity to TableInfoDTO
    private TableInfoDTO convertToDTO(TableInfo tableInfo) {
        return TableInfoDTO.builder()
                .id(tableInfo.getId())
                .name(tableInfo.getName())
                .location(tableInfo.getLocation())
                .status(tableInfo.getStatus())
                .seat_count(tableInfo.getSeat_count())
                .build();
    }

    public List<TableInfoDTO> listTables(String status, String keyword) {

        List<TableInfo> tableInfos;
        boolean hasStatus = (status != null && !status.isBlank());
        boolean hasKeyword = (keyword != null && !keyword.isBlank());

        if (hasStatus && hasKeyword) {
            tableInfos = tableRepo.findByStatusIgnoreCaseAndNameContainingIgnoreCase(status, keyword);
        }
        else if (hasStatus) {
            tableInfos = tableRepo.findByStatusIgnoreCase(status);
        }
        else if (hasKeyword) {
            tableInfos = tableRepo.findByNameContainingIgnoreCase(keyword);
        }
        else {
            tableInfos = tableRepo.findAll();
        }

        return tableInfos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
