import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-login',
  // Es un formulario reactivo, caargamos el modulo
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})

export class Login {

  authService = inject(AuthService);
  router = inject(Router);
  errorMessage = signal<string>('');

  // en los formualrios reactivos el nombre del campo tiene que coincidir con el formControlName en el html, el primer parametro es el valor por defecto
  // el segundo parametro son loa validadores de las bibliotecas de angular (podemos crear validadores tambien)
  public formLogin: FormGroup = new FormGroup({
    email: new FormControl('', [
      Validators.required,
      Validators.email
    ]),
    
    password: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
      Validators.maxLength(20)
    ])
  });

  onSubmit() {

    if (!this.formLogin.valid) {
      this.errorMessage.set('El formulario tiene campos que no son validos');
      return;
    } 

    this.errorMessage.set('');

    // Asignamos por desestructuracion, getRaValue() devuelve los valores y los asigna a las variables que coinciden en nombre, si queremos extraer
    // una en concreto seria con this.formLogin.get("email")?.value

    const {email,password} = this.formLogin.getRawValue();

    // Hacemos la llamada a la API, si sale bien guardamos los datos en la sesion, sino, imprimimos el mensaje de error.
    // hemos inyectado el router para redirigir a la pagina principal si se inicia sesion
    this.authService.login(email,password).subscribe({
      next: data => {
        this.authService.saveSession(data);
        this.router.navigate(['/']); // cada elemento del array es una parte de la ruta, inserta automaticamente las "/"
      },
      error: err => {
        // Este es el error del JSON que me envia el backend
        this.errorMessage.set(err.error.message);
      }
    });
  }
}
