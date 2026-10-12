import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './components/navbar/navbar';
import { Session } from './components/session/session';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, Session],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {

}
