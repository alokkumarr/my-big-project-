// import { Component } from '@angular/core';
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { of } from 'rxjs';
import 'hammerjs';
import { MaterialModule } from '../../../material.module';
import { RemoteFolderSelectorComponent } from './remote-folder-selector.component';

const getDirResponse = ['folder1', 'folder2', 'folder3'].map(name => ({
  isDirectory: true,
  name,
  path: '/',
  size: 3,
  subDirectories: true
}));

const createDirResponse = ['subFolder1', 'subFolder2', 'subFolder3'].map(name => ({
  isDirectory: true,
  name,
  path: '/',
  size: 1,
  subDirectories: true
}));

const fileSystemAPI = {
  getDir: () => of({data: getDirResponse}),
  createDir: () => of({data: createDirResponse})
};
const rootNode = {
  name: 'Staging',
  size: Infinity,
  isDirectory: true,
  path: 'root'
};

describe('Remote Folder Selector', () => {
  let fixture: ComponentFixture<RemoteFolderSelectorComponent>;
  beforeEach(done => {
    TestBed.configureTestingModule({
      imports: [MaterialModule],
      declarations: [RemoteFolderSelectorComponent]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(RemoteFolderSelectorComponent);
        const comp = fixture.componentInstance;
        comp.fileSystemAPI = fileSystemAPI;
        comp.rootNode = rootNode;

        fixture.detectChanges();
        done();
      });
  });

  it('should exist', () => {
    expect(fixture.componentInstance).not.toBeNull();
  });

  it('should have no data at first', () => {
    const component = fixture.componentInstance;
    const data = component.dataSource.data;
    expect(data.length).toBe(0);
  });

  it('should load children on the initial load', done => {
    const component = fixture.componentInstance;

    component.dataSource.dataChange.subscribe(data => {
      if (data.length === 0) {
        return;
      }
      const children = data[0].children;
      expect(children.length).toBeGreaterThan(0);
      done();
    });
  });
});
