import { TestBed } from '@angular/core/testing';

import { FosterHomeService } from './foster-home-service';

describe('FosterHomeService', () => {
  let service: FosterHomeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FosterHomeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
