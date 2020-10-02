import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SampleUpdateComponent } from './sample-update.component';

describe('SampleUpdateComponent', () => {
  let component: SampleUpdateComponent;
  let fixture: ComponentFixture<SampleUpdateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SampleUpdateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SampleUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
