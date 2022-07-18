package it.stefanotroia.mvpmatch.utils.security

import io.micronaut.core.annotation.NonNull
import jakarta.inject.Singleton
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import javax.validation.constraints.NotBlank

@Singleton
class BCryptPasswordEncoderService : PasswordEncoder {
    private var delegate: PasswordEncoder = BCryptPasswordEncoder()

    override fun encode(@NonNull rawPassword: @NotBlank CharSequence?): String {
        return delegate.encode(rawPassword)
    }

    override fun matches(
        @NonNull rawPassword: @NotBlank CharSequence?,
        @NonNull encodedPassword: @NotBlank String?
    ): Boolean {
        return delegate.matches(rawPassword, encodedPassword)
    }
}