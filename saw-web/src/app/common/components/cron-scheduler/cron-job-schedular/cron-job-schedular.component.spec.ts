import { NO_ERRORS_SCHEMA } from '@angular/core';
import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CronJobSchedularComponent } from './cron-job-schedular.component';
import * as moment from 'moment-timezone';

describe('Cron Job Scheduler Component', () => {
  let component: CronJobSchedularComponent;
  let fixture: ComponentFixture<CronJobSchedularComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CronJobSchedularComponent],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CronJobSchedularComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeDefined();
  });

  it('should convert local time to selected timezone', () => {
    const timezone = 'America/Los_Angeles';
    const inputTime = new Date('2019-01-02T00:00:00.000Z');
    const format = 'YYYY-MM-DD HH:mm:ss';
    const formattedTimestring = component.toSelectedTimezone(
      timezone,
      inputTime
    ) as string;
    expect(
      moment(new Date(formattedTimestring))
        .tz(timezone)
        .format(format)
    ).toEqual(
      moment(inputTime)
        .local()
        .format(format)
    );
  });

  it('should convert from specified timezone to local', () => {
    const timezone = 'America/Los_Angeles';
    const inputTime = '2019-01-02T05:30:00-08:00';
    const format = 'YYYY-MM-DD HH:mm:ss';

    const formattedDate = component.fromSelectedTimezone(timezone, inputTime);
    expect(
      moment(formattedDate)
        .local()
        .format(format)
    ).toEqual(moment.tz(inputTime, timezone).format(format));
  });
});
