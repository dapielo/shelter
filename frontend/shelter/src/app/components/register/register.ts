import { Component, inject, signal } from '@angular/core';
import { AuthService } from '../../services/auth-service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {

  authService = inject(AuthService);
  errorMessage = signal<string>('');

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
    
  }
}
