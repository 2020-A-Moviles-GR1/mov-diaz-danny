import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GesturesListComponent } from './gestures-list.component';

describe('GesturesListComponent', () => {
  let component: GesturesListComponent;
  let fixture: ComponentFixture<GesturesListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GesturesListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GesturesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
