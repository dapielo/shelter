import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditFosterhome } from './edit-fosterhome';

describe('EditFosterhome', () => {
  let component: EditFosterhome;
  let fixture: ComponentFixture<EditFosterhome>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditFosterhome],
    }).compileComponents();

    fixture = TestBed.createComponent(EditFosterhome);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
