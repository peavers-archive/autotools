import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { ThemeService } from '../../core/services/theme.service';
import { Option } from '../../core/domain/modules';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent implements OnInit {
  options$: Observable<Array<Option>> = this.themeService.getThemeOptions();

  constructor(private readonly themeService: ThemeService) {}

  ngOnInit(): void {
    const theme = this.themeService.getTheme();

    if (theme === undefined) {
      this.themeService.setTheme('deeppurple-amber');
    } else {
      this.themeService.setTheme(theme);
    }
  }

  themeChangeHandler(themeToSet): void {
    this.themeService.setTheme(themeToSet);
  }

  goToGitHub(): void {
    window.open('https://github.com/peavers/files', '_blank');
  }
}
