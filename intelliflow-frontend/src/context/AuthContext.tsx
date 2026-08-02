import React, { createContext, useContext, useState, useEffect } from 'react';
import { User, AuthState } from '../types';

interface AuthContextType extends AuthState {
  login: (token: string, user: User) => void;
  logout: () => void;
}

const defaultUser: User = {
  id: 'b5c3c6b2-0f7c-4490-82ae-e7d99d3b0816',
  username: 'alex.architect',
  email: 'alex.architect@intelliflow.ai',
  role: 'ROLE_ADMIN',
  status: 'ACTIVE',
  departmentId: 'd0fcde61-4e64-4756-af3f-4463a396e150',
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [state, setState] = useState<AuthState>({
    user: defaultUser,
    token: 'mock-jwt-token-123456789',
    isAuthenticated: true,
    isLoading: false,
  });

  useEffect(() => {
    const storedToken = localStorage.getItem('intelliflow_jwt');
    if (storedToken) {
      setState((prev) => ({
        ...prev,
        token: storedToken,
        isAuthenticated: true,
        isLoading: false,
      }));
    }
  }, []);

  const login = (token: string, user: User) => {
    localStorage.setItem('intelliflow_jwt', token);
    setState({
      user,
      token,
      isAuthenticated: true,
      isLoading: false,
    });
  };

  const logout = () => {
    localStorage.removeItem('intelliflow_jwt');
    setState({
      user: null,
      token: null,
      isAuthenticated: false,
      isLoading: false,
    });
  };

  return (
    <AuthContext.Provider value={{ ...state, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
