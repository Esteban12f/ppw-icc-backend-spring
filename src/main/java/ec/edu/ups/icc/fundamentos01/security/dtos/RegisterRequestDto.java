package ec.edu.ups.icc.fundamentos01.security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/*
 * DTO usado para registrar usuarios desde /auth/register.
 *
 * Este DTO representa los datos que el cliente debe enviar
 * cuando desea crear una nueva cuenta en el sistema.
 *
 * Se aplican validaciones para evitar registrar usuarios
 * con información incompleta o contraseñas débiles.
 */
public class RegisterRequestDto {

    /*
     * Nombre del usuario.
     *
     * @NotBlank:
     * - Evita valores nulos, vacíos o solo espacios.
     *
     * @Size:
     * - Define una longitud mínima y máxima para el nombre.
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    private String name;

    /*
     * Email del usuario.
     *
     * Se usa como identificador principal para iniciar sesión.
     *
     * @Email:
     * - Valida que tenga formato correcto de correo.
     *
     * @Size:
     * - Limita la longitud del correo en la base de datos.
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ingresar un email válido")
    @Size(max = 150, message = "El email no debe superar los 150 caracteres")
    private String email;

    /*
     * Contraseña del usuario.
     *
     * Esta contraseña NO se guarda directamente en la base de datos.
     * En el servicio de autenticación se cifra usando BCrypt.
     *
     * @Pattern:
     * - Obliga a que la contraseña tenga al menos:
     *   una mayúscula, una minúscula y un número.
     */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$",
            message = "La contraseña debe contener al menos una mayúscula, una minúscula y un número"
    )
    private String password;

    /*
     * Constructor vacío requerido por Spring y Jackson
     * para poder convertir el JSON recibido en un objeto Java.
     */
    public RegisterRequestDto() {
    }

    /*
     * Constructor lleno útil para pruebas o creación manual de objetos.
     */
    public RegisterRequestDto(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /*
     * Getters y setters.
     *
     * Son necesarios para que Spring pueda leer y escribir
     * los valores recibidos desde el cuerpo JSON.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}