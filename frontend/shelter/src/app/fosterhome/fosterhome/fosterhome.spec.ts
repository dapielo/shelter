import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Fosterhome } from './fosterhome';

describe('Fosterhome', () => {
  let component: Fosterhome;
  let fixture: ComponentFixture<Fosterhome>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Fosterhome],
    }).compileComponents();

    fixture = TestBed.createComponent(Fosterhome);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
