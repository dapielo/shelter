import { Component, inject, signal } from '@angular/core';
import { AuthService } from '../../services/auth-service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserSession } from '../../model/UserSession';
import { Router } from '@angular/router';
import { UserDTO } from '../../model/UserDTO';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {

  authService = inject(AuthService);
  errorMessage = signal<string>('');
  router = inject(Router)

  formRegister : FormGroup = new FormGroup({

    email : new FormControl('',[
      Validators.email, 
      Validators.required
    ]),

    username: new FormControl('',[
      Validators.minLength(1),
      Validators.maxLength(20),
      Validators.required
    ]),

    password: new FormControl('',[
      Validators.minLength(8),
      Validators.maxLength(20),
      Validators.required
    ]),

    passwordVerification: new FormControl('',[
      Validators.minLength(8),
      Validators.maxLength(20),
      Validators.required
    ])
  }); 

  onSubmit() {

    // Tiene que ser valido y tienen que coincidir las contraseñas
    if (!this.formRegister.valid) {
      this.errorMessage.set("Hay campos que no son validos en el formulario");
      return;
    }
    if (!this.passwordMatches()){
      this.errorMessage.set("Las contraseñas no coinciden");
      return;
    }

    // Construimos el JSON para el body
    const user: UserDTO = {
      email: this.formRegister.get("email")?.value,
      username: this.formRegister.get("username")?.value,
      password: this.formRegister.get("password")?.value      
    }

    // nos subscribimos a la peticion, si sale bien guardamos la sesion, si sale mal imprimimos error
    this.authService.register(user).subscribe({
      next: (data) => {
        this.authService.saveSession(data);
        this.router.navigate(["/"]);
      },
      error: (err) => {
        this.errorMessage.set(err.error.message);
      }
    })
  }

  // verifica que las contraseñas coincidan
  passwordMatches() : boolean {
    return this.formRegister.get("password")?.value === this.formRegister.get("passwordVerification")?.value;
  }
}
