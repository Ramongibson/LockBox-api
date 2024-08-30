package com.ramongibson.lockboxapi.controller;

import com.ramongibson.lockboxapi.model.Password;
import com.ramongibson.lockboxapi.service.PasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passwords")
@RequiredArgsConstructor
@Tag(name = "Passwords", description = "APIs for managing passwords")
public class PasswordController {

    private final PasswordService passwordService;

    @Operation(summary = "Add a new password", description = "Adds a new password to the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<Password> addPassword(@RequestParam String username, @RequestBody Password password) {
        Password createdPassword = passwordService.addPassword(username, password);
        return ResponseEntity.ok(createdPassword);
    }

    @Operation(summary = "Update an existing password", description = "Updates an existing password in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "404", description = "Password not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Password> updatePassword(@RequestParam String username, @PathVariable Long id, @RequestBody Password password) {
        try {
            Password updatedPassword = passwordService.updatePassword(username, id, password);
            return ResponseEntity.ok(updatedPassword);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @Operation(summary = "Delete a password", description = "Deletes a password from the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Password not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassword(@RequestParam String username, @PathVariable Long id) {
        try {
            passwordService.deletePassword(username, id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @Operation(summary = "Get all passwords for a user", description = "Retrieves all passwords associated with a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Passwords retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<Password>> getAllPasswords(@RequestParam String username) {
        List<Password> passwords = passwordService.getAllPasswords(username);
        return ResponseEntity.ok(passwords);
    }
}