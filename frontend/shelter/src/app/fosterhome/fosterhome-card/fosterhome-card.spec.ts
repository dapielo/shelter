import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FosterhomeCard } from './fosterhome-card';

describe('FosterhomeCard', () => {
  let component: FosterhomeCard;
  let fixture: ComponentFixture<FosterhomeCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FosterhomeCard],
    }).compileComponents();

    fixture = TestBed.createComponent(FosterhomeCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
