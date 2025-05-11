package in.batur.eksiclone.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import in.batur.eksiclone.userservice.dto.RoleDTO;
import in.batur.eksiclone.userservice.service.RoleService;

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

    @Operation(summary = "Get a role by ID", description = "Returns the details of a role with the specified ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role found"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> findById(@PathVariable Long id) {
        RoleDTO roleDTO = roleService.findById(id);
        return ResponseEntity.ok(roleDTO);
    }

    @Operation(summary = "Create a new role", 
               description = "Creates a new role with the provided details. " +
                             "Role name must be unique and follow the format 'ROLE_NAME'.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Role created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid role data or duplicate role name")
    })
    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        RoleDTO createdRole = roleService.createRole(roleDTO);
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a role", 
               description = "Updates the details of a role with the specified ID. " +
                             "Role name must remain unique and follow the format 'ROLE_NAME'.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role updated successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "400", description = "Invalid role data or duplicate role name")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDTO roleDTO) {
        roleDTO.setId(id);
        RoleDTO updatedRole = roleService.updateRole(roleDTO);
        return ResponseEntity.ok(updatedRole);
    }

    @Operation(summary = "Delete a role", 
               description = "Deletes a role with the specified ID. " +
                             "Will fail if any users are currently assigned this role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Role deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "400", description = "Role cannot be deleted as it is assigned to users")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}