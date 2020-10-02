import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FunctionUpdateComponent } from './function-update.component';

describe('FunctionUpdateComponent', () => {
  let component: FunctionUpdateComponent;
  let fixture: ComponentFixture<FunctionUpdateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FunctionUpdateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FunctionUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
