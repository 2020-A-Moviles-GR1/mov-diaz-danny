import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GestureUpdateComponent } from './gesture-update.component';

describe('GestureUpdateComponent', () => {
  let component: GestureUpdateComponent;
  let fixture: ComponentFixture<GestureUpdateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GestureUpdateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GestureUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
