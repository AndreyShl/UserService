package org.example.DTO;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;


@Data
public class UserDTO {

    private Integer id;

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name cannot exceed 50 characters")
    private String name;

    @NotBlank(message = "Surname is required")
    @Size(max = 50, message = "Surname cannot exceed 50 characters")
    private String surname;

    @NotNull(message = "Birth date is required")
    private Date birthDate;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    private boolean active;

    private Date createdAt;
    private Date updatedAt;
}
