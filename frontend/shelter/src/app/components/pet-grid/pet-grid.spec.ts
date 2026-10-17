import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PetGrid } from './pet-grid';

describe('PetGrid', () => {
  let component: PetGrid;
  let fixture: ComponentFixture<PetGrid>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PetGrid],
    }).compileComponents();

    fixture = TestBed.createComponent(PetGrid);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
