import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PendingAdoptions } from './pending-adoptions';

describe('PendingAdoptions', () => {
  let component: PendingAdoptions;
  let fixture: ComponentFixture<PendingAdoptions>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PendingAdoptions],
    }).compileComponents();

    fixture = TestBed.createComponent(PendingAdoptions);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
