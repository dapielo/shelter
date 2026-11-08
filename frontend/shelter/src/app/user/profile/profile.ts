import { Component, inject, OnInit, signal } from '@angular/core';
import { AuthService } from '../auth-service';
import { UserSession } from '../dto/UserSession';
import { Router, RouterLink } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../user-service';
import { UserUpdateDTO } from '../dto/UserUpdateDTO';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-profile',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit{

  auth: AuthService = inject(AuthService);
  userService: UserService = inject(UserService);
  router : Router = inject(Router);

  // No validamos nada excepto la contraseña, porque es el endpoint es un patch, solo enviamos la info que queremos cambiar, la contaseña si, poruqe vamos a pedir 
  // al usuario que envie la contraseña si hace algún cambio para verificar que ha sido el.
  userForm = new FormGroup({

    email: new FormControl(''),
    username: new FormControl(''),
    password: new FormControl('', Validators.required),
    newPassword: new FormControl(''),
    passwordVerification: new FormControl('')

  });

  errorMessage = signal<string>('');

  ngOnInit() {

    const user = this.auth.userSession();

    if (user == null) {
      this.router.navigate(['/login']);
    }

    this.userForm.controls.email.setValue(user?.email ?? '');
    this.userForm.controls.username.setValue(user?.username ?? '');
  }

  onSubmit() {

    // Si el formulario sale como invalido es poruqe no se ha metido la contraseña, volvemos
    if (this.userForm.invalid) {
      this.errorMessage.set("Tienes que introducir la contraseña para guardar los cambios");
      window.scrollTo({ top: 0, behavior: 'smooth' });
      return;
    }
    
    // Si las contraseñas nuevas no coinciden, volvemos
    if (this.userForm.controls.newPassword.value !== this.userForm.controls.passwordVerification.value) {
      this.errorMessage.set("Las contraseñas no coinciden");
      window.scrollTo({ top: 0, behavior: 'smooth' });
      return;
    }

    // si nada de lo anterior falla, enviamos los cambios, recogemos los valores del formgroup, con as UserUpdateDTO le indicamos que los recoga como un 
    // objeto de ese tipo, sabemos que los campos coinciden, si algun campo va como cadena vacía, el backend ya sabe que no se quiere cambiar
    const userUpdate : UserUpdateDTO = this.userForm.getRawValue() as UserUpdateDTO;
    
    this.userService.editUser(userUpdate).subscribe({
      next: (data: UserSession) => {
        this.auth.saveSession(data);
        this.errorMessage.set('');
        this.router.navigate(["/"]);
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 0) {
          this.errorMessage.set("No tienes conexion a internet, vuelve a intentarlo más tarde.");
          window.scrollTo({ top: 0, behavior: 'smooth' });
          return;
        }
        if (err.status === 403) {
          this.auth.logout("/login");
          return;
        }
        this.errorMessage.set(err.error?.message ?? 'No se ha podido realizar la solicitud');
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }
    })
  }
}
