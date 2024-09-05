import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {HomeAppliancesComponent} from './home-appliance-component/home-appliance-component.component';
import {SmartphonesComponentComponent} from './smartphones-component/smartphones-component.component';
import {TelevisionsComponentComponent} from './televisions-component/televisions-component.component';
import {FooterComponentComponent} from './footer-component/footer-component.component';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HomeAppliancesComponent, SmartphonesComponentComponent,TelevisionsComponentComponent, FooterComponentComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'home-page';
}
