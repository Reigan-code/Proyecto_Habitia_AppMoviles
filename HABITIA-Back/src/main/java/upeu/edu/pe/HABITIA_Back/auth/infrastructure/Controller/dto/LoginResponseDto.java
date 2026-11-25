package upeu.edu.pe.HABITIA_Back.auth.infrastructure.web.dto;

//    ¿Para qué sirve?
//    token: contiene el JWT generado después de autenticar correctamente al usuario.
//    tokenType: indica cómo debe enviarse el token en las siguientes peticiones. Usamos Bearer.
public class LoginResponseDto {

    private String token;
    private String tokenType;

    public LoginResponseDto() {
    }

    public LoginResponseDto(String token, String tokenType) {
        this.token = token;
        this.tokenType = tokenType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}