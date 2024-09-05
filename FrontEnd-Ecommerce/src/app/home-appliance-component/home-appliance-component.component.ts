import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-home-appliances',
  standalone: true,
  templateUrl: './home-appliance-component.component.html',
  styleUrls: ['./home-appliance-component.component.css']
})
export class HomeAppliancesComponent {
  // Component properties and methods can be defined here.
  // For example, you might define a list of appliances or logic to handle user interactions.

  appliances = [
    { name: 'Refrigerator', image: 'assets/refrigerator.png' },
    { name: 'Microwave', image: 'assets/microwave.png' },
    { name: 'Washing Machine', image: 'assets/washing-machine.png' }
  ];

  constructor() {
    // Initialization logic can go here
  }

  // Example method
  getApplianceNames() {
    return this.appliances.map(appliance => appliance.name).join(', ');
  }
}