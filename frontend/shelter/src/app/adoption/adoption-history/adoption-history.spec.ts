import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdoptionHistory } from './adoption-history';

describe('AdoptionHistory', () => {
  let component: AdoptionHistory;
  let fixture: ComponentFixture<AdoptionHistory>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdoptionHistory],
    }).compileComponents();

    fixture = TestBed.createComponent(AdoptionHistory);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
