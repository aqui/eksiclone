package in.batur.eksiclone.roleservice.controller;

import in.batur.eksiclone.roleservice.service.RoleDTO;
import in.batur.eksiclone.roleservice.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "Role API", description = "APIs for managing roles in Eksiclone")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @Operation(summary = "Get all roles", description = "Returns a list of all roles with their details")
    @GetMapping
    public List<RoleDTO> findAll() {
        return roleService.findAll();
    }
}