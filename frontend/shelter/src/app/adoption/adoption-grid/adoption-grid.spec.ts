import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdoptionGrid } from './adoption-grid';

describe('AdoptionGrid', () => {
  let component: AdoptionGrid;
  let fixture: ComponentFixture<AdoptionGrid>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdoptionGrid],
    }).compileComponents();

    fixture = TestBed.createComponent(AdoptionGrid);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
