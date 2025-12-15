import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from '../../components/homeComponents/navbar/navbar';
import { App, RouteAnimations } from '../../app';

@Component({
  selector: 'app-home-layout',
  imports: [RouterOutlet, Navbar],
  templateUrl: './home-layout.html',
  styleUrl: './home-layout.css',
  animations: [RouteAnimations]
})
export class HomeLayout {
 private app = inject(App);

  prepareRoute(outlet: RouterOutlet) {
    // return in next tick to avoid ExpressionChangedAfterItHasBeenCheckedError
    return Promise.resolve().then(() => this.app.prepareRoute(outlet));
  }

}
